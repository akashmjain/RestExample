package com.example.RESTExample.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "merchant")
    private MerchantEntity merchant;
    @ManyToOne
    @JoinColumn(name = "paymentGateway")
    private PaymentGatewayEntity paymentGateway;
    private Long amount;
    private Long totalAmount;
    private String paymentMode;
    private Timestamp timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MerchantEntity getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantEntity merchant) {
        this.merchant = merchant;
    }

    public PaymentGatewayEntity getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(PaymentGatewayEntity paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
