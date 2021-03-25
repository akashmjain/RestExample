package com.example.RESTExample.service.merchant;

import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.repository.MerchantRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.when;


public class MerchantServiceTest {

    private MerchantRepo merchantRepo;
    private MerchantService merchantService;

    @BeforeEach
    public void  beforeMethod() {
        merchantRepo = Mockito.mock(MerchantRepo.class);
        merchantService = new MerchantServiceImpl(merchantRepo);
    }

    @Test
    void testMerchantSuccessfulSave() {
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
    void testMerchantAlreadyPresent() {
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("FLIPKART");
        merchant.setUsername("akash");
        merchant.setPassword("12345");
        when(merchantRepo.findByName("FLIPKART")).thenReturn(Optional.of(merchant));
        Exception e = Assertions.assertThrows(CustomException.class, () -> {
            merchantService.save(merchant);
        });
        String expectedMessage = "Merchant already present with the same name.";
        String actualMessage = e.getMessage();
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testMerchantUsernameError() {
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
    public void afterMethod() {
        // nothing to do for now.
    }
}
