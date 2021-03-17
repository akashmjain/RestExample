package com.example.RESTExample.service;

import com.example.RESTExample.entity.PaymentGateway;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PaymentGatewayService {
    public PaymentGateway saveWithObjectNode(ObjectNode objectNode);
}
