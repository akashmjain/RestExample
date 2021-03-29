package com.example.RESTExample.service.payment;

import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.model.PaymentGatewayEntity;
import com.example.RESTExample.repository.PaymentGatewayRepo;
import com.example.RESTExample.service.merchant.MerchantService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Optional;
import static org.mockito.Mockito.when;

class PaymentGatewayServiceTest {

    private PaymentGatewayRepo paymentGatewayRepo;
    private PaymentGatewayService paymentGatewayService;
    private MerchantService merchantService;
    private ObjectMapper mapper;
    private ObjectNode objectNode;

    @BeforeEach
    void beforeMethod() {
        paymentGatewayRepo = Mockito.mock(PaymentGatewayRepo.class);
        merchantService = Mockito.mock(MerchantService.class);
        mapper = new ObjectMapper();
        paymentGatewayService = new PaymentGatewayServiceImpl(paymentGatewayRepo, merchantService, mapper);
    }

    @Test
    void testPGSuccessfulSave() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));

        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("FLIPKART");
        merchant.setUsername("akash");
        merchant.setPassword("12345");

        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(10L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("YES");
        paymentGateway.setStatus("INACTIVE");
        paymentGateway.setProcessingFee(50L);

        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(merchant));
        when(paymentGatewayRepo.save(paymentGateway)).thenReturn(paymentGateway);

        paymentGatewayService.saveWithObjectNode(objectNode);
        ObjectMapper objectMapper= new ObjectMapper();
        ObjectNode expected = objectMapper.createObjectNode();
        expected.put("success", true);
        expected.put("pgName", paymentGateway.getName());
        expected.put("status", paymentGateway.getStatus());
        Assertions.assertEquals(expected, paymentGatewayService.saveWithObjectNode(objectNode));
    }

    @Test
    void testPgNameMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));

        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Missing pgName field.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testMerchantNameMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));

        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Missing merchantName field.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testMerchantNameWithGivenValueNotPresentError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));

        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Merchant with name FLIPKART not found.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testAmountMinMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(new MerchantEntity()));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Missing amountMin or amountMax fields.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testAmountMaxMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(new MerchantEntity()));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Missing amountMin or amountMax fields.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testCardEnabledMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(new MerchantEntity()));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Missing cardEnabled field.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testNbEnabledMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(new MerchantEntity()));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Missing nbEnabled field.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testStatusMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(new MerchantEntity()));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Missing status field.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testProcessingFeeMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(new MerchantEntity()));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Missing processingFee field.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testSameNamePGAlreadyExistError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));

        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("FLIPKART");
        merchant.setUsername("akash");
        merchant.setPassword("12345");

        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(10L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("YES");
        paymentGateway.setStatus("INACTIVE");
        paymentGateway.setProcessingFee(50L);
        when(paymentGatewayRepo.findByNameAndMerchant("GPAY", merchant)).thenReturn(paymentGateway);
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(merchant));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Failed to persist, pg with same name already exists.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testPGIsAlreadyActiveError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        objectNode.set("amountMax", mapper.convertValue("1000", JsonNode.class));
        objectNode.set("cardEnabled", mapper.convertValue("No", JsonNode.class));
        objectNode.set("nbEnabled", mapper.convertValue("Yes", JsonNode.class));
        objectNode.set("status", mapper.convertValue("INACTIVE", JsonNode.class));
        objectNode.set("processingFee", mapper.convertValue("50", JsonNode.class));
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("FLIPKART");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(10L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("YES");
        paymentGateway.setStatus("ACTIVE");
        paymentGateway.setProcessingFee(50L);
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(merchant));
        when(paymentGatewayRepo.findByStatusAndMerchant("ACTIVE", merchant)).thenReturn(Optional.of(paymentGateway));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.saveWithObjectNode(objectNode));
        String expected = "Failed to persist, pg is already active for this merchant.";
        String actual = e.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    /* PG Update Test */
    @Test
    void testSuccessfulUpdate() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("FLIPKART");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName("GPAY");
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(100L);
        paymentGateway.setAmountMax(1000L);
        paymentGateway.setCardEnabled("NO");
        paymentGateway.setNbEnabled("YES");
        paymentGateway.setStatus("INACTIVE");
        paymentGateway.setProcessingFee(50L);
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(merchant));
        when(paymentGatewayRepo.save(paymentGateway)).thenReturn(paymentGateway);
        when(paymentGatewayRepo.findByNameAndMerchant("GPAY", merchant)).thenReturn(paymentGateway);
        ObjectNode expected = mapper.createObjectNode();
        expected.put("success", true);
        expected.put("pgName", paymentGateway.getName());
        expected.put("status", paymentGateway.getStatus());
        Assertions.assertEquals(expected, paymentGatewayService.updateWithObjectNode(objectNode));
    }

    @Test
    void testUpdateMerchantNotFoundError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.updateWithObjectNode(objectNode));
        String expected = "Merchant with name FLIPKART not found.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testUpdateMerchantMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.updateWithObjectNode(objectNode));
        String expected = "Missing merchantName field.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testUpdatePGNotFoundError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("FLIPKART");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(merchant));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.updateWithObjectNode(objectNode));
        String expected = "Pg not found in merchant.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testUpdatePGMissingError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("amountMin", mapper.convertValue("100", JsonNode.class));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.updateWithObjectNode(objectNode));
        String expected = "Missing pgName field.";
        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void testUpdatePGAlreadyActiveError() {
        objectNode = mapper.createObjectNode();
        objectNode.set("pgName", mapper.convertValue("GPAY", JsonNode.class));
        objectNode.set("merchantName", mapper.convertValue("FLIPKART", JsonNode.class));
        objectNode.set("status", mapper.convertValue("ACTIVE", JsonNode.class));
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("FLIPKART");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        // active payment gateway
        PaymentGatewayEntity paymentGateway1 = new PaymentGatewayEntity();
        paymentGateway1.setName("PHONEPE");
        paymentGateway1.setMerchant(merchant);
        paymentGateway1.setAmountMin(100L);
        paymentGateway1.setAmountMax(1000L);
        paymentGateway1.setCardEnabled("NO");
        paymentGateway1.setNbEnabled("YES");
        paymentGateway1.setStatus("ACTIVE");
        paymentGateway1.setProcessingFee(50L);
        // In active payment gateway.
        PaymentGatewayEntity paymentGateway2 = new PaymentGatewayEntity();
        paymentGateway2.setName("GPAY");
        paymentGateway2.setMerchant(merchant);
        paymentGateway2.setAmountMin(100L);
        paymentGateway2.setAmountMax(1000L);
        paymentGateway2.setCardEnabled("NO");
        paymentGateway2.setNbEnabled("YES");
        paymentGateway2.setStatus("INACTIVE");
        paymentGateway2.setProcessingFee(50L);
        merchant.addPaymentGatewayEntity(paymentGateway1);
        merchant.addPaymentGatewayEntity(paymentGateway2);
        when(merchantService.findByName("FLIPKART")).thenReturn(Optional.of(merchant));
        when(paymentGatewayRepo.save(paymentGateway1)).thenReturn(paymentGateway1);
        when(paymentGatewayRepo.findByNameAndMerchant("PHONEPE", merchant)).thenReturn(paymentGateway1);
        when(paymentGatewayRepo.findByNameAndMerchant("GPAY", merchant)).thenReturn(paymentGateway2);
        when(paymentGatewayRepo.findByStatusAndMerchant("ACTIVE", merchant)).thenReturn(Optional.of(paymentGateway1));
        Exception e = Assertions.assertThrows(CustomException.class, () -> paymentGatewayService.updateWithObjectNode(objectNode));
        String expected = "Failed to persist, pg is already active for this merchant.";
        Assertions.assertEquals(expected, e.getMessage());
    }
}