package com.example.RESTExample.service;

import com.example.RESTExample.entity.Merchant;

import java.util.Optional;


public interface MerchantService {
    public void save(Merchant merchant);

    public Optional<Merchant> findByName(String name);
}
