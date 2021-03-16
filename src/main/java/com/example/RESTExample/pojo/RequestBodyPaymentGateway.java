package com.example.RESTExample.pojo;

public class RequestBodyPaymentGateway {
    private String name;
    private String merchantName;
    private int amountMin;
    private int amountMax;
    private String cardEnabled;
    private String nbEnabled;
    private String status;
    private int processingFee;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getAmountMin() {
        return amountMin;
    }

    public void setAmountMin(int amountMin) {
        this.amountMin = amountMin;
    }

    public int getAmountMax() {
        return amountMax;
    }

    public void setAmountMax(int amountMax) {
        this.amountMax = amountMax;
    }

    public String getCardEnabled() {
        return cardEnabled;
    }

    public void setCardEnabled(String cardEnabled) {
        this.cardEnabled = cardEnabled;
    }

    public String getNbEnabled() {
        return nbEnabled;
    }

    public void setNbEnabled(String nbEnabled) {
        this.nbEnabled = nbEnabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(int processingFee) {
        this.processingFee = processingFee;
    }
}
