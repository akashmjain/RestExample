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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    void testUsernameMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Missing username field.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testMerchantNameMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Missing merchantName field.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testAmountMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Missing amount field.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void tesPaymentModeMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Missing paymentMode field.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testPasswordMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Missing password field.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testMerchantNotFoundError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Merchant not found.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testUsernameIsWrongError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("AMAZON");
        merchant.setUsername("vinit");
        merchant.setPassword("12345");
        when(merchantService.findByName("AMAZON")).thenReturn(Optional.of(merchant));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Username is wrong.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testPasswordIsWrongError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("AMAZON");
        merchant.setUsername("akash");
        merchant.setPassword("6789");
        when(merchantService.findByName("AMAZON")).thenReturn(Optional.of(merchant));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Password is wrong.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testNoActivePaymentError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        // merchant
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("AMAZON");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        // payment gateway
        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(10L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("YES");
        paymentGateway.setStatus("INACTIVE");
        paymentGateway.setProcessingFee(50L);
        merchant.addPaymentGatewayEntity(paymentGateway);
        when(merchantService.findByName("AMAZON")).thenReturn(Optional.of(merchant));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "No active payment for this merchant.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testAmountLimitError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("2000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        // merchant
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("AMAZON");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
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
        when(merchantService.findByName("AMAZON")).thenReturn(Optional.of(merchant));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Amount is not within limit.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testNBSetToNoError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        // merchant
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("AMAZON");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        // payment gateway
        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(10L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("NO");
        paymentGateway.setStatus("ACTIVE");
        paymentGateway.setProcessingFee(50L);
        merchant.addPaymentGatewayEntity(paymentGateway);
        when(merchantService.findByName("AMAZON")).thenReturn(Optional.of(merchant));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Net banking is not allowed.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testCardSetToNoError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("amount", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("CARD", JsonNode.class));
        objectNode.set("password", mapper.convertValue("12345", JsonNode.class));
        // merchant
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("AMAZON");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        // payment gateway
        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(10L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("NO");
        paymentGateway.setStatus("ACTIVE");
        paymentGateway.setProcessingFee(50L);
        merchant.addPaymentGatewayEntity(paymentGateway);
        when(merchantService.findByName("AMAZON")).thenReturn(Optional.of(merchant));
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.makePayment(objectNode));
        String expected = "Card banking is not allowed.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    // Get Transaction.
    @Test
    void testGetTransaction() throws Exception {
        // merchant
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("AMAZON");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        // payment gateway
        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(10L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("NO");
        paymentGateway.setStatus("ACTIVE");
        paymentGateway.setProcessingFee(50L);
        merchant.addPaymentGatewayEntity(paymentGateway);
        // transaction list
        List<TransactionEntity> list = new ArrayList<>();
        TransactionEntity t1 = new TransactionEntity();
        t1.setId(1);
        t1.setPaymentMode("NB");
        t1.setMerchant(merchant);
        t1.setTimestamp(new Timestamp(System.currentTimeMillis()));
        t1.setAmount(600L);
        t1.setPaymentGateway(paymentGateway);
        t1.setTotalAmount(t1.getAmount() + paymentGateway.getProcessingFee());
        TransactionEntity t2 = new TransactionEntity();
        t2.setId(2);
        t2.setPaymentMode("CARD");
        t2.setMerchant(merchant);
        t2.setTimestamp(new Timestamp(System.currentTimeMillis()));
        t2.setAmount(512L);
        t2.setPaymentGateway(paymentGateway);
        t2.setTotalAmount(t1.getAmount() + paymentGateway.getProcessingFee());
        list.add(t1);list.add(t2);
        when(transactionRepo.findTransactionEntityBySortOrder(1)).thenReturn(list.subList(0, 1));
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.set("merchantName", mapper.convertValue("AMAZON", JsonNode.class));
        objectNode.set("PG", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("username", mapper.convertValue("akash", JsonNode.class));
        objectNode.set("amount", mapper.convertValue(600, JsonNode.class));
        objectNode.set("totalAmount", mapper.convertValue(650, JsonNode.class));
        objectNode.set("paymentMode", mapper.convertValue("NB", JsonNode.class));
        List<ObjectNode> objectNodeList = new ArrayList<>();
        objectNodeList.add(objectNode);
        JSONAssert.assertEquals(objectNodeList.toString(), transactionService.getTransactions(1).toString(), JSONCompareMode.LENIENT);
    }

    @Test
    void testGetTransactionValueNotProperError() {
        Exception e = Assertions.assertThrows(CustomException.class, () -> transactionService.getTransactions(-1));
        String expected = "Please provide proper value parameter.";
        Assertions.assertEquals(expected, e.getMessage());
    }
}