package com.example.RESTExample.service.payment;

import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.model.PaymentGatewayEntity;
import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.repository.PaymentGatewayRepo;
import com.example.RESTExample.service.merchant.MerchantService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private final String PAYMENT_GATEWAY_CONST_STATUS_ACTIVE = "ACTIVE";
    private final String PAYMENT_GATEWAY_CONST_STATUS_INACTIVE = "INACTIVE";
    private final String PAYMENT_GATEWAY_CONST_ENABLE_YES = "YES";
    private final String PAYMENT_GATEWAY_CONST_ENABLE_NO = "NO";


    private final String PAYMENT_GATEWAY_API_PAYMENT_GATEWAY_NAME = "pgName";
    private final String PAYMENT_GATEWAY_API_MERCHANT_NAME = "merchantName";
    private final String PAYMENT_GATEWAY_API_AMOUNT_MIN = "amountMin";
    private final String PAYMENT_GATEWAY_API_AMOUNT_MAX = "amountMax";
    private final String PAYMENT_GATEWAY_API_CARD_ENABLED = "cardEnabled";
    private final String PAYMENT_GATEWAY_API_NB_ENABLED = "nbEnabled";
    private final String PAYMENT_GATEWAY_API_STATUS = "status";
    private final String PAYMENT_GATEWAY_API_PROCESSING_FEE = "processingFee";

    @Autowired
    PaymentGatewayRepo paymentGatewayRepo;

    @Autowired
    MerchantService merchantService;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public ObjectNode saveWithObjectNode(ObjectNode objectNode) {
        // Checking if user with given name is present or not. if not then giving error response back.
        if (merchantService.findByName(objectNode.get(PAYMENT_GATEWAY_API_MERCHANT_NAME).asText()).isEmpty()) {
            throw new CustomException("Merchant with name " + objectNode.get(PAYMENT_GATEWAY_API_MERCHANT_NAME).asText() + " not found.");
        }
        MerchantEntity merchantEntity = merchantService.findByName(objectNode.get(PAYMENT_GATEWAY_API_MERCHANT_NAME).asText()).get();
        // check if active payment gateway is present.
        for (PaymentGatewayEntity pg : merchantEntity.getPaymentGateways()) {
            if (pg.getStatus().equals(PAYMENT_GATEWAY_CONST_STATUS_ACTIVE) || pg.getName().equals(objectNode.get(PAYMENT_GATEWAY_API_PAYMENT_GATEWAY_NAME).asText())) {
                throw new CustomException("Failed to persist,pg is already active for this merchant. or pg with same name already exist.");
            }
        }
        // validate min,max amount
        if (objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MAX).asInt() < objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MIN).asInt() || objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MIN).asInt() < 0 || objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MAX).asInt() < 0) {
            throw new CustomException("Min Max values are wrong");
        }
        // validate cardEnabled
        if (!objectNode.get(PAYMENT_GATEWAY_API_CARD_ENABLED).asText().toUpperCase(Locale.ROOT).equals(PAYMENT_GATEWAY_CONST_ENABLE_YES) && !objectNode.get(PAYMENT_GATEWAY_API_CARD_ENABLED).asText().toUpperCase(Locale.ROOT).equals(PAYMENT_GATEWAY_CONST_ENABLE_NO)) {
            throw new CustomException("Invalid cardEnable Field");
        }
        // validate nbEnabled
        if (!objectNode.get(PAYMENT_GATEWAY_API_NB_ENABLED).asText().toUpperCase(Locale.ROOT).equals(PAYMENT_GATEWAY_CONST_ENABLE_YES) && !objectNode.get(PAYMENT_GATEWAY_API_NB_ENABLED).asText().toUpperCase(Locale.ROOT).equals(PAYMENT_GATEWAY_CONST_ENABLE_NO)) {
            throw new CustomException("Invalid nbEnable Field");
        }
        // validate status
        if (!objectNode.get(PAYMENT_GATEWAY_API_STATUS).asText().toUpperCase(Locale.ROOT).equals(PAYMENT_GATEWAY_CONST_STATUS_ACTIVE) && !objectNode.get(PAYMENT_GATEWAY_API_STATUS).asText().toUpperCase(Locale.ROOT).equals(PAYMENT_GATEWAY_CONST_STATUS_INACTIVE)) {
            throw new CustomException("Invalid status field");
        }
        if (objectNode.get(PAYMENT_GATEWAY_API_PROCESSING_FEE).asInt() < 0) {
            throw new CustomException("Invalid Processing fees");
        }
        PaymentGatewayEntity paymentGatewayEntity = new PaymentGatewayEntity();
        paymentGatewayEntity.setName(objectNode.get(PAYMENT_GATEWAY_API_PAYMENT_GATEWAY_NAME).asText());
        paymentGatewayEntity.setMerchant(merchantEntity);
        paymentGatewayEntity.setAmountMin(objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MIN).asLong());
        paymentGatewayEntity.setAmountMax(objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MAX).asLong());
        paymentGatewayEntity.setCardEnabled(objectNode.get(PAYMENT_GATEWAY_API_CARD_ENABLED).asText().toUpperCase(Locale.ROOT));
        paymentGatewayEntity.setNbEnabled(objectNode.get(PAYMENT_GATEWAY_API_NB_ENABLED).asText().toUpperCase(Locale.ROOT));
        paymentGatewayEntity.setStatus(objectNode.get(PAYMENT_GATEWAY_API_STATUS).asText());
        paymentGatewayEntity.setProcessingFee(objectNode.get(PAYMENT_GATEWAY_API_PROCESSING_FEE).asLong());
        paymentGatewayRepo.save(paymentGatewayEntity);
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("pgName", paymentGatewayEntity.getName());
        msg.put("status", paymentGatewayEntity.getStatus());
        return msg;
    }

    @Override
    public ObjectNode updateWithObjectNode(ObjectNode objectNode) {
        // check for required fields and throw error like so=
        requiredFieldsCheck(objectNode);
        JsonNode pgName = objectNode.get(PAYMENT_GATEWAY_API_PAYMENT_GATEWAY_NAME);
        JsonNode merchantName = objectNode.get(PAYMENT_GATEWAY_API_MERCHANT_NAME);
        // validate if merchant with given name is present or not
        Optional<MerchantEntity> merchant = merchantService.findByName(merchantName.asText());
        if (merchant.isEmpty()) {
            throw new CustomException("Merchant with name " + objectNode.get(PAYMENT_GATEWAY_API_MERCHANT_NAME).asText() + " not present.");
        }
        // validate if pg with given name is present within merchant or not.
        PaymentGatewayEntity paymentGateway = null;
        for (PaymentGatewayEntity pg : merchant.get().getPaymentGateways()) {
            if (pg.getName().equalsIgnoreCase(pgName.asText())) {
                paymentGateway = pg;
                break;
            }
        }
        if (paymentGateway == null) {
            throw new CustomException("Payment Gateway is not present in given merchant");
        }
        if (objectNode.get(PAYMENT_GATEWAY_API_CARD_ENABLED) != null) {
            validateCardEnabled(objectNode.get(PAYMENT_GATEWAY_API_CARD_ENABLED));
            paymentGateway.setCardEnabled(objectNode.get(PAYMENT_GATEWAY_API_CARD_ENABLED).asText());
        }
        if (objectNode.get(PAYMENT_GATEWAY_API_NB_ENABLED) != null) {
            validateNbEnabled(objectNode.get(PAYMENT_GATEWAY_API_NB_ENABLED));
            paymentGateway.setNbEnabled(objectNode.get(PAYMENT_GATEWAY_API_NB_ENABLED).asText());
        }
        if (objectNode.get(PAYMENT_GATEWAY_API_STATUS) != null) {
            validateStatus(objectNode.get(PAYMENT_GATEWAY_API_STATUS));
            paymentGateway.setStatus(objectNode.get(PAYMENT_GATEWAY_API_STATUS).asText());
        }
        if (objectNode.get(PAYMENT_GATEWAY_API_PROCESSING_FEE) != null) {
            validateProcessingFee(objectNode.get(PAYMENT_GATEWAY_API_PROCESSING_FEE));
            paymentGateway.setProcessingFee(objectNode.get(PAYMENT_GATEWAY_API_PROCESSING_FEE).asLong());
        }
//        checkAndSetAmountMin(objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MIN), objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MAX), paymentGateway);
//        checkAndSetAmountMax(objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MIN), objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MAX), paymentGateway);
//        checkAndSetCardEnabled(objectNode.get(PAYMENT_GATEWAY_API_CARD_ENABLED), paymentGateway);
//        checkAndSetNbEnabled(objectNode.get(PAYMENT_GATEWAY_API_NB_ENABLED), paymentGateway);
//        checkAndSetStatus(objectNode.get(PAYMENT_GATEWAY_API_STATUS), paymentGateway);
//        checkAndSetProcessingFee(objectNode.get(PAYMENT_GATEWAY_API_PROCESSING_FEE), paymentGateway);
        return null;
    }

    private void checkAndSetStatus(JsonNode status, PaymentGatewayEntity paymentGateway) {
        if (status != null) {
            validateStatus(status);
            paymentGateway.setStatus(status.asText());
        }
    }

    private void validateStatus(JsonNode status) {
        if (!status.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_STATUS_ACTIVE) && !status.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_STATUS_INACTIVE)) {
            throw new CustomException("Invalid status field");
        }
    }

    private void checkAndSetProcessingFee(JsonNode processingFee, PaymentGatewayEntity paymentGateway) {
        if (processingFee != null) {
            validateProcessingFee(processingFee);
            paymentGateway.setProcessingFee(processingFee.asLong());
        }
    }

    private void validateProcessingFee(JsonNode processingFee) {
        if (processingFee.asLong() < 0) {
            throw new CustomException("Invalid Processing fees");
        }
    }

    private void checkAndSetNbEnabled(JsonNode nbEnabled, PaymentGatewayEntity paymentGateway) {
        if (nbEnabled != null) {
            validateNbEnabled(nbEnabled);
            paymentGateway.setNbEnabled(nbEnabled.asText());

        }
    }

    private void validateNbEnabled(JsonNode nbEnabled) {
        if (!nbEnabled.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_ENABLE_YES) && !nbEnabled.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_ENABLE_NO)) {
            throw new CustomException("Invalid nbEnable Field");
        }
    }

    private void checkAndSetCardEnabled(JsonNode cardEnabled, PaymentGatewayEntity paymentGateway) {
        if (cardEnabled != null) {
            validateCardEnabled(cardEnabled);
            paymentGateway.setCardEnabled(cardEnabled.asText());
        }
    }

    private void validateCardEnabled(JsonNode cardEnabled) {
        if (!cardEnabled.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_ENABLE_YES) && !cardEnabled.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_ENABLE_NO)) {
            throw new CustomException("Invalid cardEnable Field");
        }
    }

    private void checkAndSetAmountMax(JsonNode amountMin, JsonNode amountMax, PaymentGatewayEntity paymentGateway) {
        if (amountMax != null) {
            validateAmountMaxAndMin(amountMin, amountMax, paymentGateway);
            paymentGateway.setAmountMin(aMax);
        }
    }

    private void validateAmountMaxAndMin(long aMin, long aMax, PaymentGatewayEntity pg) {
        if (aMax <= 0 || aMin < 0 || aMax < aMin) {
            throw new CustomException("Please provide proper min max amount.");
        }
    }

    private void checkAndSetAmountMin(JsonNode amountMin, JsonNode amountMax, PaymentGatewayEntity paymentGateway) {
        if (amountMin != null) {
            long aMin = amountMin.asLong();
            long aMax = amountMax == null ? paymentGateway.getAmountMax() : amountMax.asLong();
            if (aMin >= 0 && aMin < aMax) {
                paymentGateway.setAmountMin(aMin);
            } else {
                throw new CustomException("Please provide proper amountMin property");
            }
        }
    }

    private void requiredFieldsCheck(ObjectNode objectNode) {
        if (objectNode.get(PAYMENT_GATEWAY_API_PAYMENT_GATEWAY_NAME) == null) {
            throw new CustomException("Please provide pgName");
        }
        if (objectNode.get(PAYMENT_GATEWAY_API_MERCHANT_NAME) == null) {
            throw new CustomException("Please provide merchantName");
        }
    }
}
