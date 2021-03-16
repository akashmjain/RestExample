package com.example.RESTExample.controller;

import com.example.RESTExample.entity.Merchant;
import com.example.RESTExample.entity.PaymentGateway;
import com.example.RESTExample.pojo.RequestBodyPaymentGateway;
import com.example.RESTExample.service.MerchantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class MerchantController {

    private final String ACTIVE_STATUS = "ACTIVE";
    private final String INACTIVE_STATUS = "INACTIVE";
    private final String ENABLE_YES = "YES";
    private final String ENABLE_NO = "NO";

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/createMerchant")
    public String createMerchant(@RequestBody Merchant merchant) throws Exception {
        if (merchantService.findByName(merchant.getName()).isPresent()) {
            ObjectNode data = objectMapper.createObjectNode();
            data.put("success", false);
            data.put("errorMessage", "Failed to persist, duplicate merchant found.");
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } else {
            merchantService.save(merchant);
            ObjectNode msg = objectMapper.createObjectNode();
            msg.put("success", true);
            msg.put("merchantName", merchant.getName());
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
        }
    }

    @PostMapping("/createPG")
    public String createPaymentGateway(@RequestBody RequestBodyPaymentGateway requestBodyPaymentGateway) throws Exception {
        // Checking if user with given name is present or not. if not then giving error response back.
        if (merchantService.findByName(requestBodyPaymentGateway.getMerchantName()).isEmpty()) {
            ObjectNode msg = objectMapper.createObjectNode();
            msg.put("success", false);
            msg.put("errorMessage", "Merchant with name " + requestBodyPaymentGateway.getMerchantName() + " not found.");
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
        }
        Merchant merchant = merchantService.findByName(requestBodyPaymentGateway.getMerchantName()).get();
        // check if active payment gateway is present.
        for (PaymentGateway paymentGateway : merchant.getPaymentGateways()) {
            if (paymentGateway.getStatus().equals(ACTIVE_STATUS) && !paymentGateway.getName().equals(requestBodyPaymentGateway.getName())) {
                ObjectNode msg = objectMapper.createObjectNode();
                msg.put("success", false);
                msg.put("errorMessage", "Failed to persist,pg is already active for this merchant");
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
            }
        }
        if (requestBodyPaymentGateway.getAmountMax() < requestBodyPaymentGateway.getAmountMin() || requestBodyPaymentGateway.getAmountMin() < 0 || requestBodyPaymentGateway.getAmountMax() < 0) {
            ObjectNode msg = objectMapper.createObjectNode();
            msg.put("success", false);
            msg.put("errorMessage", "Max Min value error");
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
        }
        if (!requestBodyPaymentGateway.getCardEnabled().equals(ENABLE_YES) || !requestBodyPaymentGateway.getCardEnabled().equals(ENABLE_NO)) {
            ObjectNode msg = objectMapper.createObjectNode();
            msg.put("success", false);
            msg.put("errorMessage", "Invalid card value");
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
        }
        if (!requestBodyPaymentGateway.getNbEnabled().equals(ENABLE_YES) || !requestBodyPaymentGateway.getNbEnabled().equals(ENABLE_NO)) {
            ObjectNode msg = objectMapper.createObjectNode();
            msg.put("success", false);
            msg.put("errorMessage", "Invalid Net Banking value");
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
        }
        return null;
    }


}
