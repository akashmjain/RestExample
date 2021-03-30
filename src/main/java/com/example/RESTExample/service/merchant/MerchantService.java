package com.example.RESTExample.service.merchant;

import com.example.RESTExample.model.MerchantEntity;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;


public interface MerchantService {

    public ObjectNode save(MerchantEntity merchantEntity);

    public Optional<MerchantEntity> findByName(String name);

    public Optional<MerchantEntity> findByUsername(String username);
}

