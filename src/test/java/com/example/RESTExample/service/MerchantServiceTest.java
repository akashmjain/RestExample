package com.example.RESTExample.service;

import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.repository.MerchantRepo;
import com.example.RESTExample.service.merchant.MerchantService;
import com.example.RESTExample.service.merchant.MerchantServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import static org.mockito.Mockito.when;


public class MerchantServiceTest {
    
    private MerchantRepo merchantRepo;
    private MerchantService merchantService;

    @BeforeEach
    public void  initMethod() {
        merchantRepo = Mockito.mock(MerchantRepo.class);
        merchantService = new MerchantServiceImpl(merchantRepo);

    }

    @Test
    void testMerchantSuccessfulSave() throws Exception {
        MerchantEntity merchantEntity = new MerchantEntity();
        merchantEntity.setName("FLIPKART");
        merchantEntity.setUsername("akash");
        merchantEntity.setPassword("12345");
        when(merchantRepo.save(merchantEntity)).thenReturn(merchantEntity);
        ObjectMapper objectMapper= new ObjectMapper();
        ObjectNode expected = objectMapper.createObjectNode();
        expected.put("success", true);
        expected.put("merchantName", merchantEntity.getName());
        Assertions.assertEquals(expected, merchantService.save(merchantEntity));
    }

    @Test
    void testMerchantUsernameError()  {
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("FLIPKART10");
        merchant.setPassword("12345");
        Exception e = Assertions.assertThrows(CustomException.class, () -> {
            merchantService.save(merchant);
        });
        String expectedMessage = "Please insert username in your request.";
        String actualMessage = e.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMerchantPasswordError() {
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("FLIPKART");
        merchant.setUsername("akash");
        Exception e = Assertions.assertThrows(CustomException.class, () -> {
            merchantService.save(merchant);
        });
        String expectedMessage = "Please provide password in your request.";
        String actualMessage = e.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMerchantNameError() {
        MerchantEntity merchant = new MerchantEntity();
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        Exception e = Assertions.assertThrows(CustomException.class, () -> {
            merchantService.save(merchant);
        });
        String expectedMessage = "Please insert name in your request.";
        String actualMessage = e.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @AfterEach
    public void endMethod() {
//        merchantRepo.deleteByName("FLIPKART10");
    }
}
