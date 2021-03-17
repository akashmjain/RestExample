package com.example.RESTExample.service;

import com.example.RESTExample.entity.Merchant;
import com.example.RESTExample.entity.PaymentGateway;
import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.repository.PaymentGatewayRepo;
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

    @Override
    public PaymentGateway saveWithObjectNode(ObjectNode objectNode) {
        // Checking if user with given name is present or not. if not then giving error response back.
        if (merchantService.findByName(objectNode.get(MERCHANT_NAME).asText()).isEmpty()) {
            throw new CustomException("Merchant with name " + objectNode.get(MERCHANT_NAME).asText() + " not found.");
        }
        Merchant merchant = merchantService.findByName(objectNode.get(MERCHANT_NAME).asText()).get();
        // check if active payment gateway is present.
        for (PaymentGateway pg : merchant.getPaymentGateways()) {
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
        PaymentGateway paymentGateway = new PaymentGateway();
        paymentGateway.setName(objectNode.get(PAYMENT_GATEWAY_NAME).asText());
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(objectNode.get(AMOUNT_MIN).asInt());
        paymentGateway.setAmountMax(objectNode.get(AMOUNT_MAX).asInt());
        paymentGateway.setCardEnabled(objectNode.get(CARD_ENABLED).asText().toUpperCase(Locale.ROOT));
        paymentGateway.setNbEnabled(objectNode.get(NB_ENABLED).asText().toUpperCase(Locale.ROOT));
        paymentGateway.setStatus(objectNode.get(STATUS).asText());
        paymentGateway.setProcessingFee(objectNode.get(PROCESSING_FEE).asInt());
        paymentGatewayRepo.save(paymentGateway);
        return paymentGateway;
    }

}
