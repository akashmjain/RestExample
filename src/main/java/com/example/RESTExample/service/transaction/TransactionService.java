package com.example.RESTExample.service.transaction;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface TransactionService {
    public ObjectNode makePayment(ObjectNode objectNode);
}
