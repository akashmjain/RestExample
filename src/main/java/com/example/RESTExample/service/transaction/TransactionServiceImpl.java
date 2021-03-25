package com.example.RESTExample.service.transaction;

import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.model.PaymentGatewayEntity;
import com.example.RESTExample.model.TransactionEntity;
import com.example.RESTExample.repository.TransactionRepo;
import com.example.RESTExample.service.merchant.MerchantService;
import com.fasterxml.jackson.databind.JsonNode;
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
    private JsonNode username;
    private JsonNode merchantName;
    private JsonNode amount;
    private JsonNode paymentMode;
    private JsonNode password;
    TransactionRepo transactionRepo;
    MerchantService merchantService;
    ObjectMapper objectMapper;

    public TransactionServiceImpl(TransactionRepo transactionRepo, MerchantService merchantService, ObjectMapper objectMapper) {
        this.transactionRepo = transactionRepo;
        this.merchantService = merchantService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode makePayment(ObjectNode objectNode) {
        username = objectNode.get(TRANSACTION_API_USERNAME);
        merchantName = objectNode.get(TRANSACTION_API_MERCHANT_NAME);
        amount = objectNode.get(TRANSACTION_API_AMOUNT);
        paymentMode = objectNode.get(TRANSACTION_API_PAYMENT_MODE);
        password = objectNode.get(TRANSACTION_API_PASSWORD);

        validateUsername(username);
        validateMerchantName(merchantName);
        validateAmount(amount);
        validatePaymentMode(paymentMode);
        validatePassword(password);
        Optional<MerchantEntity> merchantEntity = merchantService.findByName(merchantName.asText());
        if (merchantEntity.isEmpty()) {
            throw new CustomException("No such merchant present");
        }
        if (!merchantEntity.get().getUsername().equals(username.asText())) {
            throw new CustomException("Username is wrong");
        }
        if (!merchantEntity.get().getPassword().equals(password.asText())) {
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
        if (amount.asLong() < activePaymentGateway.getAmountMin() || amount.asLong() > activePaymentGateway.getAmountMax()) {
            throw new CustomException("amount is not within limit");
        }
        if (paymentMode.asText().equalsIgnoreCase("NB")) {
            if (activePaymentGateway.getNbEnabled().equals("NO")) {
                throw new CustomException("Net Banking is not allowed");
            }
        } else if (paymentMode.asText().equalsIgnoreCase("CARD")) {
            if (activePaymentGateway.getCardEnabled().equals("NO")) {
                throw new CustomException("Card banking is not allowed");
            }
        }
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setPaymentGateway(activePaymentGateway);
        transactionEntity.setMerchant(merchantEntity.get());
        transactionEntity.setAmount(amount.asLong());
        transactionEntity.setTotalAmount(amount.asLong() + activePaymentGateway.getProcessingFee());
        transactionEntity.setPaymentMode(paymentMode.asText());
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

    private void validateUsername(JsonNode username) {
        if (username == null) {
            throw new CustomException("Missing username field.");
        }
    }

    private void validateMerchantName(JsonNode merchantName) {
        if (merchantName == null) {
            throw new CustomException("Missing merchantName field.");
        }

        if (merchantService.findByName(merchantName.asText()).isEmpty()) {
            throw new CustomException("User not found");
        }
    }

    private void validateAmount(JsonNode amount) {
        if (amount == null) {
            throw new CustomException("Missing amount field.");
        }
    }

    private void validatePaymentMode(JsonNode paymentMode) {
        if (paymentMode == null) {
            throw new CustomException("Missing paymentMode field.");
        }
    }

    private void validatePassword(JsonNode password) {
        if (password == null) {
            throw new CustomException("Missing password field.");
        }
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

