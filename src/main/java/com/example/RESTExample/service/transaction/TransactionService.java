package com.example.RESTExample.service.transaction;

import com.example.RESTExample.model.TransactionEntity;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public interface TransactionService {
    public ObjectNode makePayment(ObjectNode objectNode);
    public List<ObjectNode> getTransactions(int value);
}
