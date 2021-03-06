package com.example.RESTExample.repository;

import com.example.RESTExample.model.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepo extends JpaRepository<MerchantEntity, Integer> {
    public Optional<MerchantEntity> findByName(String name);
    public Optional<MerchantEntity> findByUsername(String username);
    public void deleteByName(String name);
}
