package com.example.RESTExample.service.transaction;

import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.model.PaymentGatewayEntity;
import com.example.RESTExample.model.TransactionEntity;
import com.example.RESTExample.repository.TransactionRepo;
import com.example.RESTExample.service.merchant.MerchantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {
    final private String TRANSACTION_API_USERNAME = "username";
    final private String TRANSACTION_API_MERCHANT_NAME = "merchantName";
    final private String TRANSACTION_API_AMOUNT = "amount";
    final private String TRANSACTION_API_PAYMENT_MODE = "paymentMode";
    final private String TRANSACTION_API_PASSWORD = "password";
    @Autowired
    MerchantService merchantService;
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public ObjectNode makePayment(ObjectNode objectNode) {
        String  username = objectNode.get(TRANSACTION_API_USERNAME).asText();
        String  merchantName = objectNode.get(TRANSACTION_API_MERCHANT_NAME).asText();
        Long  amount = objectNode.get(TRANSACTION_API_AMOUNT).asLong();
        String  paymentMode = objectNode.get(TRANSACTION_API_PAYMENT_MODE).asText();
        String  password = objectNode.get(TRANSACTION_API_PASSWORD).asText();

        Optional<MerchantEntity> merchantEntity = merchantService.findByName(merchantName);
        if (merchantEntity.isEmpty()) {
            throw new CustomException("User not found");
        }
        if (!merchantEntity.get().getUsername().equals(username)) {
            throw new CustomException("Username is wrong");
        }
        if (!merchantEntity.get().getPassword().equals(password)) {
            throw new CustomException("Password is wrong");
        }
        // get active payment gateway.
        PaymentGatewayEntity activePaymentGateway = null;
        for (PaymentGatewayEntity pg : merchantEntity.get().getPaymentGatewayEntities()) {
            if (pg.getStatus().equals("ACTIVE")) {
                activePaymentGateway = pg;
                break;
            }
        }
        if (activePaymentGateway == null) {
            throw new CustomException("No active payment for this merchant");
        }
        if (amount < activePaymentGateway.getAmountMin() || amount > activePaymentGateway.getAmountMax()) {
            throw new CustomException("amount is not within limit");
        }
        if (paymentMode.equalsIgnoreCase("NB")) {
            if (activePaymentGateway.getNbEnabled().equals("NO")) {
                throw new CustomException("Net Banking is not allowed");
            }
        } else if (paymentMode.equalsIgnoreCase("CARD")) {
            if (activePaymentGateway.getCardEnabled().equals("NO")) {
                throw new CustomException("Card banking is not allowed");
            }
        }
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setPaymentGateway(activePaymentGateway);
        transactionEntity.setMerchant(merchantEntity.get());
        transactionEntity.setAmount(amount);
        transactionEntity.setTotalAmount(amount + activePaymentGateway.getProcessingFee());
        transactionEntity.setPaymentMode(paymentMode);
        transactionEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        transactionRepo.save(transactionEntity);
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("amount", transactionEntity.getAmount());
        msg.put("totalAmount", transactionEntity.getTotalAmount());
        msg.put("merchantName", transactionEntity.getMerchant().getName());
        msg.put("pgName", transactionEntity.getPaymentGateway().getName());
        return msg;
    }

    @Override
    public List<ObjectNode> getTransactions(int value) {
        List<TransactionEntity> transactionEntities = transactionRepo.findTransactionEntityBySortOrder(value);
        List<ObjectNode> objectNodes = new ArrayList<>();
        transactionEntities.forEach(trans -> {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("merchantName", trans.getMerchant().getName());
            objectNode.put("PG", trans.getPaymentGateway().getName());
            objectNode.put("username", trans.getMerchant().getUsername());
            objectNode.put("amount", trans.getAmount());
            objectNode.put("totalAmount", trans.getTotalAmount());
            objectNode.put("paymentMode", trans.getPaymentMode());
            objectNodes.add(objectNode);
        });
        return objectNodes;
    }
}

