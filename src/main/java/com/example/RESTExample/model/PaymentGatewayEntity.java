package com.example.RESTExample.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class PaymentGatewayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "merchant")
    private MerchantEntity merchant;
    private Long amountMin;
    private Long amountMax;
    private String cardEnabled;
    private String nbEnabled;
    private String status;
    private Long processingFee;
    @OneToMany(mappedBy = "paymentGateway")
    private List<TransactionEntity> transactions;

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

    public MerchantEntity getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantEntity merchant) {
        this.merchant = merchant;
    }

    public Long getAmountMin() {
        return amountMin;
    }

    public void setAmountMin(Long amountMin) {
        this.amountMin = amountMin;
    }

    public Long getAmountMax() {
        return amountMax;
    }

    public void setAmountMax(Long amountMax) {
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

    public Long getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(Long processingFee) {
        this.processingFee = processingFee;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }
}
