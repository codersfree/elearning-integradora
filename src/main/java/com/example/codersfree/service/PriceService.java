package com.example.codersfree.service;

import com.example.codersfree.model.Price;
import com.example.codersfree.repository.PriceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Transactional(readOnly = true)
    public List<Price> findAll() {
        return priceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Price findById(Long id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Precio no encontrado con id: " + id));
    }
}