package com.example.RESTExample.service;

import com.example.RESTExample.entity.PaymentGateway;
import com.example.RESTExample.repository.PaymentGatewayRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    @Autowired
    PaymentGatewayRepo paymentGatewayRepo;

    @Override
    public void save(PaymentGateway paymentGateway) {
        paymentGatewayRepo.save(paymentGateway);
    }
}
