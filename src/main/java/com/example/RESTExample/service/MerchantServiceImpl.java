package com.example.RESTExample.service;

import com.example.RESTExample.error.CustomException;
import com.example.RESTExample.model.MerchantEntity;
import com.example.RESTExample.repository.MerchantRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    MerchantRepo merchantRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public ObjectNode save(MerchantEntity merchantEntity) {
        if (this.findByName(merchantEntity.getName()).isPresent()) {
            throw new CustomException("Merchant already present with the same name");
        }
        merchantRepo.save(merchantEntity);
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("success", true);
        msg.put("merchantName", merchantEntity.getName());
        return msg;
    }

    @Override
    public Optional<MerchantEntity> findByName(String name) {
        return merchantRepo.findByName(name);
    }


}
