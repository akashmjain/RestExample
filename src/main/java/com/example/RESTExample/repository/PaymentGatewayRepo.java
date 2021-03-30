package com.example.RESTExample.repository;

import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.model.PaymentGatewayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentGatewayRepo extends JpaRepository<PaymentGatewayEntity, Integer> {
    PaymentGatewayEntity findByNameAndMerchant(String asText, MerchantEntity merchant);
    Optional<PaymentGatewayEntity> findByStatusAndMerchant(String status, MerchantEntity merchantEntity);
}
