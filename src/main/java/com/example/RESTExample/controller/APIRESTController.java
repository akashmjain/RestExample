package com.example.RESTExample.controller;

import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.error.CustomErrorResponse;
import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.model.PaymentGatewayEntity;
import com.example.RESTExample.model.TransactionEntity;
import com.example.RESTExample.service.merchant.MerchantService;
import com.example.RESTExample.service.payment.PaymentGatewayService;
import com.example.RESTExample.service.transaction.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class APIRESTController {
    @Autowired
    MerchantService merchantService;

    @Autowired
    PaymentGatewayService paymentGatewayService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/createMerchant")
    public ResponseEntity<String> createMerchant(@RequestBody MerchantEntity merchantEntity) throws Exception {
        ObjectNode res = merchantService.save(merchantEntity);
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
    }

    @PostMapping("/createPG")
    public ResponseEntity<String> createPaymentGateway(@RequestBody ObjectNode objectNode) throws Exception {
        ObjectNode res = paymentGatewayService.saveWithObjectNode(objectNode);
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
    }

    @PostMapping("/makePayment")
    public ResponseEntity<String> makePayment(@RequestBody ObjectNode objectNode) throws Exception {
        ObjectNode res = transactionService.makePayment(objectNode);
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
    }

    @GetMapping("/lastTransaction")
    public ResponseEntity<String> getTransactions(@RequestParam("value") int value) throws Exception {
        List<ObjectNode> objectNodes = transactionService.getTransactions(value);
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNodes));
    }

    @PutMapping("/updatePG")
    public ResponseEntity<String> updatePaymentGateway(@RequestBody ObjectNode objectNode) throws Exception {
        ObjectNode res = paymentGatewayService.updateWithObjectNode(objectNode);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
    }

    @ExceptionHandler
    public ResponseEntity<CustomErrorResponse> handleMerchantException(CustomException merchantException) {
        CustomErrorResponse error = new CustomErrorResponse();
        error.setErrorMessage(merchantException.getMessage());
        error.setSuccess(false);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
