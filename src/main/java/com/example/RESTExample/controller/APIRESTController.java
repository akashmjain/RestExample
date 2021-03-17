package com.example.RESTExample.controller;

import com.example.RESTExample.entity.Merchant;
import com.example.RESTExample.entity.PaymentGateway;
import com.example.RESTExample.error.CustomErrorResponse;
import com.example.RESTExample.error.CustomException;
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


@RestController
public class APIRESTController {
    @Autowired
    MerchantService merchantService;

    @Autowired
    PaymentGatewayService paymentGatewayService;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/createMerchant")
    public ResponseEntity<String> createMerchant(@RequestBody Merchant merchant) throws Exception {
        if (merchantService.findByName(merchant.getName()).isPresent()) {
            throw new CustomException("Merchant already present with the same name");
        }
        merchantService.save(merchant);
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("merchantName", merchant.getName());
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg));
    }

    @PostMapping("/createPG")
    public ResponseEntity<String> createPaymentGateway(@RequestBody ObjectNode objectNode) throws Exception {
        PaymentGateway paymentGateway = paymentGatewayService.saveWithObjectNode(objectNode);
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("pgName", paymentGateway.getName());
        msg.put("status", paymentGateway.getStatus());
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg));
    }

    @PostMapping("/test")
    public ResponseEntity<String> testRequest(@RequestBody ObjectNode objectNode) throws Exception{
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("pgName", "EZETAP");
        msg.put("status", "ACTIVE");
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg));
    }

    @PostMapping("/makePayment")
    public ResponseEntity<String> makePayment() {
        
    }

    @ExceptionHandler
    public ResponseEntity<CustomErrorResponse> handleMerchantException(CustomException merchantException) {
        CustomErrorResponse error = new CustomErrorResponse();
        error.setErrorMessage(merchantException.getMessage());
        error.setSuccess(false);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
