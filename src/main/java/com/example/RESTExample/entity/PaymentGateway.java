package com.example.RESTExample.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PaymentGateway {
    @Id
    private int id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "merchant")
    private Merchant merchant;
    private int amountMin;
    private int amountMax;
    private boolean cardEnabled;
    private boolean nbEnabled;
    private String status;
    private int processingFee;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
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

    public boolean getCardEnabled() {
        return cardEnabled;
    }

    public void setCardEnabled(boolean cardEnabled) {
        this.cardEnabled = cardEnabled;
    }

    public boolean getNbEnabled() {
        return nbEnabled;
    }

    public void setNbEnabled(boolean nbEnabled) {
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
