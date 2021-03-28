package com.example.RESTExample.controller;

import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.service.merchant.MerchantService;
import com.example.RESTExample.service.payment.PaymentGatewayService;
import com.example.RESTExample.service.transaction.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APIRESTController.class)
class APIRESTControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MerchantService merchantService;
    @MockBean
    private PaymentGatewayService paymentGatewayService;
    @MockBean
    private TransactionService transactionService;
    private ObjectMapper mapper;

    @BeforeEach
    void beforeMethod() {
        mapper = new ObjectMapper();
    }

    @Test
    void testPostCreateNewMerchantSuccessfully() throws Exception {
        ObjectNode result = mapper.createObjectNode();
        result.put("success", true);
        result.put("merchantName", "FLIPKART");
        Mockito.when(merchantService.save(Mockito.any(MerchantEntity.class))).thenReturn(result);
        String request = "{\n" +
                "    \"name\": \"FLIPKART\",\n" +
                "    \"username\": \"akash\",\n" +
                "    \"password\": \"12345\"\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/createMerchant/")
            .content(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        String expected = result.toString();
        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @Test
    void testPostCreateNewPaymentGatewaySuccessfully() throws Exception {
        ObjectNode result = mapper.createObjectNode();
        result.put("success", true);
        result.put("pgName", "PHONEPE");
        result.put("status", "INACTIVE");
        Mockito.when(paymentGatewayService.saveWithObjectNode(Mockito.any(ObjectNode.class))).thenReturn(result);
        String request = "{\n" +
                "    \"pgName\": \"PHONEPE\",\n" +
                "    \"merchantName\": \"FLIPKART\",\n" +
                "    \"amountMin\": \"100\",\n" +
                "    \"amountMax\": \"1000\",\n" +
                "    \"cardEnabled\": \"No\",\n" +
                "    \"nbEnabled\": \"Yes\",\n" +
                "    \"status\": \"INACTIVE\",\n" +
                "    \"processingFee\": \"50\"\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/createPG/")
            .content(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        String expected = result.toString();
        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    // one less field. this test check for 8 fields should be provided not more not less.
    @Test
    void testPostCreateNewPaymentNotProperlyFormattedError() throws Exception {
        String request = "{\n" +
                "    \"pgName\": \"PHONEPE\",\n" +
                "    \"merchantName\": \"FLIPKART\",\n" +
                "    \"amountMin\": \"100\",\n" +
                "    \"amountMax\": \"1000\",\n" +
                "    \"nbEnabled\": \"Yes\",\n" +
                "    \"status\": \"INACTIVE\",\n" +
                "    \"processingFee\": \"50\"\n" +
                "}";
        String expected = "{" +
                "\"success\":false," +
                "\"errorMessage\":\"Please provide properly formatted request.\"" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/createPG/")
            .content(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(builder)
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @Test
    void testPutUpdatePGSuccessfully() throws Exception {
        ObjectNode result = mapper.createObjectNode();
        result.put("success", true);
        result.put("pgName", "PHONEPE");
        result.put("status", "INACTIVE");
        Mockito.when(paymentGatewayService.updateWithObjectNode(Mockito.any(ObjectNode.class))).thenReturn(result);
        String request = "{\n" +
                "    \"pgName\": \"PHONEPE\",\n" +
                "    \"merchantName\": \"FLIPKART\",\n" +
                "    \"processingFee\": \"150\"\n" +
                "}";
        String expected = result.toString();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/updatePG/")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @Test
    void testPutUpdatePGNotProperlyFormattedError() throws Exception {
        String request = "{\n" +
                "    \"pgName\": \"PHONEPE\",\n" +
                "    \"merchantName\": \"FLIPKART\",\n" +
                "    \"amountMin\": \"100\",\n" +
                "    \"amountMax\": \"1000\",\n" +
                "    \"cardEnabled\": \"Yes\",\n" +
                "    \"nbEnabled\": \"Yes\",\n" +
                "    \"status\": \"INACTIVE\",\n" +
                "    \"extraField\": \"value\",\n" +
                "    \"processingFee\": \"100\"\n" +
                "}";
        String expected = "{" +
                "   \"success\":false," +
                "   \"errorMessage\":\"Please provide properly formatted request.\"" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/updatePG/")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @Test
    void testPostMakePaymentSuccessfully() throws Exception {
        ObjectNode result = mapper.createObjectNode();
        result.put("success", true);
        result.put("amount", "600");
        result.put("totalAmount", "650");
        result.put("merchantName", "FLIPKART");
        result.put("pgName", "GPAY");
        String request = "{\n" +
                "    \"username\": \"akash\",\n" +
                "    \"merchantName\": \"FLIPKART\",\n" +
                "    \"amount\": \"600\",\n" +
                "    \"paymentMode\": \"NB\",\n" +
                "    \"password\": \"12345\"\n" +
                "}";
        Mockito.when(transactionService.makePayment(Mockito.any(ObjectNode.class))).thenReturn(result);
        String expected = result.toString();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/makePayment/")
            .content(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @Test
    void testPostMakePaymentNotProperlyFormattedError() throws Exception {
        String request = "{\n" +
                "    \"merchantName\": \"FLIPKART\",\n" +
                "    \"amount\": \"600\",\n" +
                "    \"paymentMode\": \"NB\",\n" +
                "    \"password\": \"12345\"\n" +
                "}";
        String expected = "{" +
                "   \"success\":false," +
                "   \"errorMessage\":\"Please provide properly formatted request.\"" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/makePayment/")
            .content(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(builder)
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @Test
    void testGetLastTransaction() throws Exception {
        List<ObjectNode> objectNodes = new ArrayList<>();
        ObjectNode trans1 = mapper.createObjectNode();
        trans1.put("merchantName", "FLIPKART");
        trans1.put("PG", "GAPY");
        trans1.put("username", "akash");
        trans1.put("amount", "250");
        trans1.put("totalAmount", "300");
        trans1.put("paymentMode", "NB");
        ObjectNode trans2 = mapper.createObjectNode();
        trans2.put("merchantName", "AMAZON");
        trans2.put("PG", "GAPY");
        trans2.put("username", "vinit");
        trans2.put("amount", "420");
        trans2.put("totalAmount", "480");
        trans2.put("paymentMode", "CARD");
        objectNodes.add(trans1);
        objectNodes.add(trans2);
        Mockito.when(transactionService.getTransactions(1)).thenReturn(objectNodes.subList(0, 1));
        Mockito.when(transactionService.getTransactions(2)).thenReturn(objectNodes.subList(0, 2));
        String expected = objectNodes.toString();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/lastTransaction?value=2")
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(expected));
    }
}