package com.example.RESTExample.service.transaction;


import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.model.PaymentGatewayEntity;
import com.example.RESTExample.model.TransactionEntity;
import com.example.RESTExample.repository.TransactionRepo;
import com.example.RESTExample.service.merchant.MerchantService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    private MerchantService merchantService;
    private TransactionRepo transactionRepo;
    private TransactionService transactionService;
    private ObjectMapper mapper;
    private ObjectNode objectNode;

    @BeforeEach
    public void beforeMethod() {
        merchantService = Mockito.mock(MerchantService.class);
        transactionRepo = Mockito.mock(TransactionRepo.class);
        mapper = new ObjectMapper();
        transactionService = new TransactionServiceImpl(transactionRepo, merchantService, mapper);
    }

    @Test
    void testSuccessfulPaymentMade() {
        // request
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        // merchant
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName(objectNode.get("merchantName").asText());
        merchant.setUsername(objectNode.get("username").asText());
        merchant.setPassword(objectNode.get("password").asText());
        // payment gateway
        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(10L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("YES");
        paymentGateway.setStatus("ACTIVE");
        paymentGateway.setProcessingFee(50L);
        merchant.addPaymentGatewayEntity(paymentGateway);
        // transaction entity.
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setPaymentMode(objectNode.get("paymentMode").asText());
        transactionEntity.setMerchant(merchant);
        transactionEntity.setPaymentGateway(paymentGateway);
        transactionEntity.setAmount(1000L);
        transactionEntity.setTotalAmount(transactionEntity.getAmount() + paymentGateway.getProcessingFee());
        when(transactionRepo.save(transactionEntity)).thenReturn(new TransactionEntity());
        when(merchantService.findByName("AMAZON")).thenReturn(Optional.of(merchant));
        ObjectNode expected = mapper.createObjectNode();
        expected.put("success", true);
        expected.put("amount", transactionEntity.getAmount());
        expected.put("totalAmount", transactionEntity.getTotalAmount());
        expected.put("merchantName", transactionEntity.getMerchant().getName());
        expected.put("pgName", transactionEntity.getPaymentGateway().getName());
        Assertions.assertEquals(expected, transactionService.makePayment(objectNode));
    }

    @Test
    void testUserNotFoundError() {
        // request
        objectNode = mapper.createObjectNode();

        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        // merchant
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName(objectNode.get("merchantName").asText());
        merchant.setUsername(objectNode.get("username").asText());
        merchant.setPassword(objectNode.get("password").asText());
        // payment gateway
        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(10L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("YES");
        paymentGateway.setStatus("ACTIVE");
        paymentGateway.setProcessingFee(50L);
        merchant.addPaymentGatewayEntity(paymentGateway);
        // transaction entity.
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setPaymentMode(objectNode.get("paymentMode").asText());
        transactionEntity.setMerchant(merchant);
        transactionEntity.setPaymentGateway(paymentGateway);
        transactionEntity.setAmount(1000L);
        transactionEntity.setTotalAmount(transactionEntity.getAmount() + paymentGateway.getProcessingFee());
        when(transactionRepo.save(transactionEntity)).thenReturn(new TransactionEntity());
        when(merchantService.findByName("AMAZON")).thenReturn(Optional.of(merchant));
    }
}