package com.example.RESTExample.repository;

import com.example.RESTExample.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TransactionRepo extends JpaRepository<TransactionEntity, Integer> {

    @Query(value = "SELECT * FROM transaction_entity t ORDER BY t.timestamp DESC LIMIT :value", nativeQuery = true)
    List<TransactionEntity> findTransactionEntityBySortOrder(@Param("value") int value);
}
