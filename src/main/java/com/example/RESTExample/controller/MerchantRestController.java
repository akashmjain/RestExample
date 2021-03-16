package com.example.RESTExample.controller;

import com.example.RESTExample.entity.Merchant;
import com.example.RESTExample.entity.PaymentGateway;
import com.example.RESTExample.error.CustomErrorResponse;
import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.pojo.RequestBodyPaymentGateway;
import com.example.RESTExample.service.MerchantService;
import com.example.RESTExample.service.PaymentGatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class MerchantRestController {
    private final String STATUS_ACTIVE = "ACTIVE";
    private final String STATUS_INACTIVE = "INACTIVE";
    private final String ENABLE_YES = "YES";
    private final String ENABLE_NO = "NO";

    @Autowired
    MerchantService merchantService;

    @Autowired
    PaymentGatewayService paymentGatewayService;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/createMerchant")
    public String createMerchant(@RequestBody Merchant merchant) throws Exception {
        if (merchantService.findByName(merchant.getName()).isPresent()) {
            throw new CustomException("Merchant already present with the same name");
        }
        merchantService.save(merchant);
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("merchantName", merchant.getName());
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
    }

    @PostMapping("/createPG")
    public void createPaymentGateway(@RequestBody RequestBodyPaymentGateway requestBodyPaymentGateway) {
        // Checking if user with given name is present or not. if not then giving error response back.
        if (merchantService.findByName(requestBodyPaymentGateway.getMerchantName()).isEmpty()) {
            throw new CustomException("Merchant with name " + requestBodyPaymentGateway.getMerchantName() + " not found.");
        }
        Merchant merchant = merchantService.findByName(requestBodyPaymentGateway.getMerchantName()).get();
        // check if active payment gateway is present.
        for (PaymentGateway pg : merchant.getPaymentGateways()) {
            if (pg.getStatus().equals("ACTIVE") && !pg.getName().equals(requestBodyPaymentGateway.getName())) {
                throw new CustomException("Failed to persist,pg is already active for this merchant");
            }
        }
        // validate min,max amount
        if (requestBodyPaymentGateway.getAmountMax() < requestBodyPaymentGateway.getAmountMin() || requestBodyPaymentGateway.getAmountMin() < 0 || requestBodyPaymentGateway.getAmountMax() < 0) {
            throw new CustomException("Min Max values are wrong");
        }
        // validate cardEnabled
        if (!requestBodyPaymentGateway.getCardEnabled().toUpperCase(Locale.ROOT).equals(ENABLE_YES) || !requestBodyPaymentGateway.getCardEnabled().toUpperCase(Locale.ROOT).equals(ENABLE_NO)) {
            throw new CustomException("Invalid cardEnable Field");
        }
        // validate nbEnabled
        if (!requestBodyPaymentGateway.getNbEnabled().toUpperCase(Locale.ROOT).equals(ENABLE_YES) || !requestBodyPaymentGateway.getNbEnabled().toUpperCase(Locale.ROOT).equals(ENABLE_NO)) {
            throw new CustomException("Invalid nbEnable Field");
        }
        // validate status
        if (!requestBodyPaymentGateway.getStatus().toUpperCase(Locale.ROOT).equals(STATUS_ACTIVE) || !requestBodyPaymentGateway.getStatus().toUpperCase(Locale.ROOT).equals(STATUS_INACTIVE)) {
            throw new CustomException("Invalid status field");
        }
        PaymentGateway paymentGateway = new PaymentGateway();
        paymentGateway.setName(requestBodyPaymentGateway.getName());
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(requestBodyPaymentGateway.getAmountMin());
        paymentGateway.setAmountMax(requestBodyPaymentGateway.getAmountMax());
        paymentGateway.setCardEnabled(requestBodyPaymentGateway.getCardEnabled().equals(ENABLE_YES));
        paymentGateway.setNbEnabled(requestBodyPaymentGateway.getNbEnabled().equals(ENABLE_YES));
        paymentGateway.setStatus(requestBodyPaymentGateway.getStatus());
        paymentGatewayService.save(paymentGateway);
    }

    @ExceptionHandler
    public ResponseEntity<CustomErrorResponse> handleMerchantException(CustomException merchantException) {
        CustomErrorResponse error = new CustomErrorResponse();
        error.setErrorMessage(merchantException.getMessage());
        error.setSuccess(false);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
