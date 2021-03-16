package com.example.RESTExample.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Merchant {

    @Id
    private int id;
    @Column(unique = true, nullable = false)
    private String name;
    private String username;
    private String password;
    @OneToMany(mappedBy = "merchant")
    private List<PaymentGateway> paymentGateways;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<PaymentGateway> getPaymentGateways() {
        return paymentGateways;
    }

    public void setPaymentGateways(List<PaymentGateway> paymentGateways) {
        this.paymentGateways = paymentGateways;
    }
}
