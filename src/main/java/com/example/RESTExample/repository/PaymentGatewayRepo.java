package com.example.RESTExample.repository;

import com.example.RESTExample.entity.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentGatewayRepo extends JpaRepository<PaymentGateway, Integer> {
}
