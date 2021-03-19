package com.example.RESTExample.repository;

import com.example.RESTExample.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransactionRepo extends JpaRepository<TransactionEntity, Integer> {

}
