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
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Optional;

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
    //@TODO write this entire function again
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
        for (PaymentGatewayEntity pg : merchantEntity.get().getPaymentGateways()) {
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
        transactionEntity.setId(0);
        transactionRepo.save(transactionEntity);


        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("amount", transactionEntity.getAmount());
        msg.put("totalAmount", transactionEntity.getTotalAmount());
        msg.put("merchantName", transactionEntity.getMerchant().getName());
        msg.put("pgName", transactionEntity.getPaymentGateway().getName());
        return msg;
    }

    /*@Override
    public ObjectNode makePayment(ObjectNode objectNode) {
        Optional<MerchantEntity> merchant = merchantService.findByUsername(objectNode.get(TRANSACTION_API_USERNAME).asText());
        // check if username is present or not.
        if (merchant.isEmpty()) {
            throw new CustomException("User with username : "+ objectNode.get(TRANSACTION_API_USERNAME).asText() +" not found");
        }
        // checking password matches the records or not.
        if (!objectNode.get(TRANSACTION_API_PASSWORD).asText().equals(merchant.get().getPassword())) {
            System.out.println(objectNode.get(TRANSACTION_API_PASSWORD).asText());
            System.out.println(merchant.get().getPassword());
            throw new CustomException("Authentication failure");
        }
        // get a active payment gateway.
        PaymentGatewayEntity activePaymentGateway = null;
        for (PaymentGatewayEntity pg : merchant.get().getPaymentGateways()) {
            if (pg.getStatus().equals("ACTIVE")) {
                activePaymentGateway = pg;
                break;
            }
        }
        // if active payment gateway not found give error back.
        if (activePaymentGateway == null) {
            throw new CustomException("No active payment gateway is present");
        }
        // check if amount to be transacted is within limits or not.
        if (objectNode.get(TRANSACTION_API_AMOUNT).asLong() < activePaymentGateway.getAmountMin() || objectNode.get(TRANSACTION_API_AMOUNT).asLong() > activePaymentGateway.getAmountMax()) {
            throw new CustomException("Amount is out of range");
        }
        // check if given payment mode is active or not.
        String paymentMode = objectNode.get(TRANSACTION_API_PAYMENT_MODE).asText();
        if (paymentMode.equalsIgnoreCase("CARD")) {
            if (activePaymentGateway.getCardEnabled().equals("NO")) {
                throw new CustomException("Card Payment is not active");
            }
        } else if (paymentMode.equalsIgnoreCase("NB")) {
            if (activePaymentGateway.getNbEnabled().equals("NO")) {
                throw new CustomException("Nb payment is not active");
            }
        }

        System.out.println(merchant.get().getName());
        System.out.println(objectNode.get(TRANSACTION_API_AMOUNT).asLong());
        System.out.println(paymentMode);
        System.out.println(activePaymentGateway.getName());
        System.out.println(objectNode.get(TRANSACTION_API_AMOUNT).asLong() + activePaymentGateway.getProcessingFee());


//        TransactionEntity transactionEntity = new TransactionEntity();
//        transactionEntity.setMerchant(merchant.get());
//        transactionEntity.setAmount(objectNode.get(TRANSACTION_API_AMOUNT).asLong());
//        transactionEntity.setPaymentMode(paymentMode);
//        transactionEntity.setPaymentGateway(activePaymentGateway);
//        transactionEntity.setTotalAmount(objectNode.get(TRANSACTION_API_AMOUNT).asLong() + activePaymentGateway.getProcessingFee());
//        transactionEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
//        transactionRepo.save(transactionEntity);
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
//        msg.put("amount", transactionEntity.getAmount());
//        msg.put("totalAmount", transactionEntity.getTotalAmount());
//        msg.put("merchantName", transactionEntity.getMerchant().getName());
//        msg.put("pgName", transactionEntity.getPaymentGateway().getName());
        return msg;
    }*/
}

