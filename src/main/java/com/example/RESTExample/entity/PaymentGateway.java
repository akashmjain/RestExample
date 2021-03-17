package com.example.RESTExample.entity;

import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Entity
public class PaymentGateway {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "merchant")
    private Merchant merchant;
    private int amountMin;
    private int amountMax;
    private String cardEnabled;
    private String nbEnabled;
    private String status;
    private int processingFee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
