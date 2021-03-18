package com.example.RESTExample.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class MerchantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String name;
    private String username;
    private String password;
    @OneToMany(mappedBy = "merchant")
    private List<PaymentGatewayEntity> paymentGatewayEntities;
    @OneToMany(mappedBy = "merchant")
    private List<TransactionEntity> transactions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<PaymentGatewayEntity> getPaymentGateways() {
        return paymentGatewayEntities;
    }

    public void setPaymentGateways(List<PaymentGatewayEntity> paymentGatewayEntities) {
        this.paymentGatewayEntities = paymentGatewayEntities;
    }

    public List<PaymentGatewayEntity> getPaymentGatewayEntities() {
        return paymentGatewayEntities;
    }

    public void setPaymentGatewayEntities(List<PaymentGatewayEntity> paymentGatewayEntities) {
        this.paymentGatewayEntities = paymentGatewayEntities;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }
}