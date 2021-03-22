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

import java.util.List;
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


    public ObjectNode saveWithObjectNodeV2(ObjectNode objectNode) {
        JsonNode pgName = objectNode.get(PAYMENT_GATEWAY_API_PAYMENT_GATEWAY_NAME);
        JsonNode merchantName = objectNode.get(PAYMENT_GATEWAY_API_MERCHANT_NAME);
        JsonNode amountMin = objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MIN);
        JsonNode amountMax = objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MAX);
        JsonNode cardEnabled = objectNode.get(PAYMENT_GATEWAY_API_CARD_ENABLED);
        JsonNode nbEnabled = objectNode.get(PAYMENT_GATEWAY_API_NB_ENABLED);
        JsonNode status = objectNode.get(PAYMENT_GATEWAY_API_STATUS);
        JsonNode processingFee = objectNode.get(PAYMENT_GATEWAY_API_PROCESSING_FEE);
        // null check
        if (pgName == null || merchantName == null || amountMin == null || amountMax == null || cardEnabled == null || nbEnabled == null || status == null || processingFee == null) {
            throw new CustomException("Please provide all required fields.");
        }
        // validation
        validateMerchant(merchantName);
        MerchantEntity merchant = merchantService.findByName(merchantName.asText()).get();
        validatePaymentGateway(pgName, merchant.getPaymentGateways());
        validateAmountMinAndMax(amountMin.asLong(), amountMax.asLong());
        validateStatus(status);
        validateProcessingFee(processingFee);
        validateNbEnabled(nbEnabled);
        validateCardEnabled(cardEnabled);

        // save pg
        PaymentGatewayEntity paymentGateway = new PaymentGatewayEntity();
        paymentGateway.setName(pgName.asText());
        paymentGateway.setMerchant(merchant);
        paymentGateway.setAmountMin(amountMin.asLong());
        paymentGateway.setAmountMax(amountMax.asLong());
        paymentGateway.setCardEnabled(cardEnabled.asText().toUpperCase(Locale.ROOT));
        paymentGateway.setNbEnabled(nbEnabled.asText().toUpperCase(Locale.ROOT));
        paymentGateway.setStatus(status.asText());
        paymentGateway.setProcessingFee(processingFee.asLong());
        paymentGatewayRepo.save(paymentGateway);

        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("pgName", paymentGateway.getName());
        msg.put("status", paymentGateway.getStatus());
        return msg;
    }

    public ObjectNode saveWithObjectNodeV1(ObjectNode objectNode) {
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

    public ObjectNode updateWithObjectNodeV2(ObjectNode objectNode) {
        JsonNode pgName = objectNode.get(PAYMENT_GATEWAY_API_PAYMENT_GATEWAY_NAME);
        JsonNode merchantName = objectNode.get(PAYMENT_GATEWAY_API_MERCHANT_NAME);
        JsonNode amountMin = objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MIN);
        JsonNode amountMax = objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MAX);
        JsonNode cardEnabled = objectNode.get(PAYMENT_GATEWAY_API_CARD_ENABLED);
        JsonNode nbEnabled = objectNode.get(PAYMENT_GATEWAY_API_NB_ENABLED);
        JsonNode status = objectNode.get(PAYMENT_GATEWAY_API_STATUS);
        JsonNode processingFee = objectNode.get(PAYMENT_GATEWAY_API_PROCESSING_FEE);

        Optional<MerchantEntity> merchant = merchantService.findByName(merchantName.asText());
        if (merchant.isEmpty()) {
            throw new CustomException("Merchant not found");
        }
        PaymentGatewayEntity pg = paymentGatewayRepo.findByNameAndMerchant(pgName.asText(), merchant.get());
        if (pg == null) {
            throw new CustomException("Pg not found in merchant");
        }

        if (amountMin != null) {
            long aMin = amountMin.asLong();
            long aMax = amountMax == null ? pg.getAmountMax() : amountMax.asLong();
            validateAmountMinAndMax(aMin, aMax);
            pg.setAmountMin(aMin);
        }
        if (amountMax != null) {
            long aMin = amountMin == null ? pg.getAmountMin() : amountMin.asLong();
            long aMax = amountMax.asLong();
            validateAmountMinAndMax(aMin, aMax);
            pg.setAmountMax(aMax);
        }
        if (cardEnabled != null) {
            validateCardEnabled(cardEnabled);
            pg.setCardEnabled(cardEnabled.asText());
        }
        if (nbEnabled != null) {
            validateNbEnabled(nbEnabled);
            pg.setNbEnabled(nbEnabled.asText());
        }
        if (status != null) {
            validateStatus(status);
            pg.setStatus(status.asText());
        }
        if (processingFee != null) {
            validateProcessingFee(processingFee);
            pg.setProcessingFee(processingFee.asLong());
        }

        return null;
    }

    public ObjectNode updateWithObjectNodeV1(ObjectNode objectNode) {
        // check for required fields and throw error like so
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
        long aMin = objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MIN) == null ? paymentGateway.getAmountMin() : objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MIN).asLong();
        long aMax = objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MAX) == null ? paymentGateway.getAmountMax() : objectNode.get(PAYMENT_GATEWAY_API_AMOUNT_MAX).asLong();
        validateAmountMinAndMax(aMin, aMax);
        paymentGateway.setAmountMin(aMin);
        paymentGateway.setAmountMax(aMax);
        paymentGatewayRepo.save(paymentGateway);
        return null;
    }

    private void validateMerchant(JsonNode merchantName) {
        if (merchantService.findByName(merchantName.asText()).isEmpty()) {
            throw new CustomException("Merchant with name " + merchantName.asText() + " not found.");
        }
    }

    private void validatePaymentGateway(JsonNode pgName, List<PaymentGatewayEntity> pgs) {
        for (PaymentGatewayEntity pg : pgs) {
            if (pg.getStatus().equals(PAYMENT_GATEWAY_CONST_STATUS_ACTIVE) || pg.getName().equals(pgName.asText())) {
                throw new CustomException("Failed to persist, pg is already active for this merchant. or pg with same name already exist.");
            }
        }
    }

    private void validateStatus(JsonNode status) {
        if (!status.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_STATUS_ACTIVE) && !status.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_STATUS_INACTIVE)) {
            throw new CustomException("Invalid status field");
        }
    }

    private void validateProcessingFee(JsonNode processingFee) {
        if (processingFee.asLong() < 0) {
            throw new CustomException("Invalid Processing fees");
        }
    }

    private void validateNbEnabled(JsonNode nbEnabled) {
        if (!nbEnabled.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_ENABLE_YES) && !nbEnabled.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_ENABLE_NO)) {
            throw new CustomException("Invalid nbEnable Field");
        }
    }

    private void validateCardEnabled(JsonNode cardEnabled) {
        if (!cardEnabled.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_ENABLE_YES) && !cardEnabled.asText().equalsIgnoreCase(PAYMENT_GATEWAY_CONST_ENABLE_NO)) {
            throw new CustomException("Invalid cardEnable Field");
        }
    }

    private void validateAmountMinAndMax(long aMin, long aMax) {
        if (aMax <= 0 || aMin < 0 || aMax < aMin) {
            throw new CustomException("Please provide proper min max amount.");
        }
    }

    // only for update
    private void requiredFieldsCheck(ObjectNode objectNode) {
        if (objectNode.get(PAYMENT_GATEWAY_API_PAYMENT_GATEWAY_NAME) == null) {
            throw new CustomException("Please provide pgName");
        }
        if (objectNode.get(PAYMENT_GATEWAY_API_MERCHANT_NAME) == null) {
            throw new CustomException("Please provide merchantName");
        }
    }

    @Override
    public ObjectNode saveWithObjectNode(ObjectNode objectNode) {
        return saveWithObjectNodeV2(objectNode);
    }

    @Override
    public ObjectNode updateWithObjectNode(ObjectNode objectNode) {
        return updateWithObjectNodeV2(objectNode);
    }
}
