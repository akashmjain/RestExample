package com.example.RESTExample.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Override
    public ObjectNode makePayment(ObjectNode objectNode) {
        return null;
    }
}
