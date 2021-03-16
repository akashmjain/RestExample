package com.example.RESTExample.repository;

import com.example.RESTExample.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepo extends JpaRepository<Merchant, Integer> {
    public Optional<Merchant> findByName(String name);
}
