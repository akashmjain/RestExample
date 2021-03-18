package com.example.RESTExample.service;

import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.model.PaymentGatewayEntity;
import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.repository.PaymentGatewayRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private final String STATUS_ACTIVE = "ACTIVE";
    private final String STATUS_INACTIVE = "INACTIVE";
    private final String ENABLE_YES = "YES";
    private final String ENABLE_NO = "NO";

    private final String PAYMENT_GATEWAY_NAME = "name";
    private final String MERCHANT_NAME = "merchantName";
    private final String AMOUNT_MIN = "amountMin";
    private final String AMOUNT_MAX = "amountMax";
    private final String CARD_ENABLED = "cardEnabled";
    private final String NB_ENABLED = "nbEnabled";
    private final String STATUS = "status";
    private final String PROCESSING_FEE = "processingFee";

    @Autowired
    PaymentGatewayRepo paymentGatewayRepo;

    @Autowired
    MerchantService merchantService;

    @Autowired
    ObjectMapper objectMapper;
    @Override
    public ObjectNode saveWithObjectNode(ObjectNode objectNode) {
        // Checking if user with given name is present or not. if not then giving error response back.
        if (merchantService.findByName(objectNode.get(MERCHANT_NAME).asText()).isEmpty()) {
            throw new CustomException("Merchant with name " + objectNode.get(MERCHANT_NAME).asText() + " not found.");
        }
        MerchantEntity merchantEntity = merchantService.findByName(objectNode.get(MERCHANT_NAME).asText()).get();
        // check if active payment gateway is present.
        for (PaymentGatewayEntity pg : merchantEntity.getPaymentGateways()) {
            if (pg.getStatus().equals(STATUS_ACTIVE) || pg.getName().equals(objectNode.get(PAYMENT_GATEWAY_NAME).asText())) {
                throw new CustomException("Failed to persist,pg is already active for this merchant. or pg with same name already exist.");
            }
        }
        // validate min,max amount
        if (objectNode.get(AMOUNT_MAX).asInt() < objectNode.get(AMOUNT_MIN).asInt() || objectNode.get(AMOUNT_MIN).asInt() < 0 || objectNode.get(AMOUNT_MAX).asInt() < 0) {
            throw new CustomException("Min Max values are wrong");
        }
        // validate cardEnabled
        if (!objectNode.get(CARD_ENABLED).asText().toUpperCase(Locale.ROOT).equals(ENABLE_YES) && !objectNode.get(CARD_ENABLED).asText().toUpperCase(Locale.ROOT).equals(ENABLE_NO)) {
            throw new CustomException("Invalid cardEnable Field");
        }
        // validate nbEnabled
        if (!objectNode.get(NB_ENABLED).asText().toUpperCase(Locale.ROOT).equals(ENABLE_YES) && !objectNode.get(NB_ENABLED).asText().toUpperCase(Locale.ROOT).equals(ENABLE_NO)) {
            throw new CustomException("Invalid nbEnable Field");
        }
        // validate status
        if (!objectNode.get(STATUS).asText().toUpperCase(Locale.ROOT).equals(STATUS_ACTIVE) && !objectNode.get(STATUS).asText().toUpperCase(Locale.ROOT).equals(STATUS_INACTIVE)) {
            throw new CustomException("Invalid status field");
        }
        if (objectNode.get(PROCESSING_FEE).asInt() < 0) {
            throw new CustomException("Invalid Processing fees");
        }
        PaymentGatewayEntity paymentGatewayEntity = new PaymentGatewayEntity();
        paymentGatewayEntity.setName(objectNode.get(PAYMENT_GATEWAY_NAME).asText());
        paymentGatewayEntity.setMerchant(merchantEntity);
        paymentGatewayEntity.setAmountMin(objectNode.get(AMOUNT_MIN).asLong());
        paymentGatewayEntity.setAmountMax(objectNode.get(AMOUNT_MAX).asLong());
        paymentGatewayEntity.setCardEnabled(objectNode.get(CARD_ENABLED).asText().toUpperCase(Locale.ROOT));
        paymentGatewayEntity.setNbEnabled(objectNode.get(NB_ENABLED).asText().toUpperCase(Locale.ROOT));
        paymentGatewayEntity.setStatus(objectNode.get(STATUS).asText());
        paymentGatewayEntity.setProcessingFee(objectNode.get(PROCESSING_FEE).asLong());
        paymentGatewayRepo.save(paymentGatewayEntity);
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("pgName", paymentGatewayEntity.getName());
        msg.put("status", paymentGatewayEntity.getStatus());
        return msg;
    }

}
