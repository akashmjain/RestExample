package com.example.RESTExample.repository;

import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.model.PaymentGatewayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentGatewayRepo extends JpaRepository<PaymentGatewayEntity, Integer> {
    PaymentGatewayEntity findByNameAndMerchant(String asText, MerchantEntity merchant);
}
