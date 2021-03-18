package com.example.RESTExample.service;

import com.example.RESTExample.model.PaymentGatewayEntity;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PaymentGatewayService {
    public ObjectNode saveWithObjectNode(ObjectNode objectNode);
}
