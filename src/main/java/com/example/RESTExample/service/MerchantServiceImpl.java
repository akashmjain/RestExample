package com.example.RESTExample.service;

import com.example.RESTExample.entity.Merchant;
import com.example.RESTExample.repository.MerchantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    MerchantRepo merchantRepo;

    @Override
    public void save(Merchant merchant) {
        merchantRepo.save(merchant);
    }

    @Override
    public Optional<Merchant> findByName(String name) {
        return merchantRepo.findByName(name);
    }
}
