package com.example.codersfree.service;

import com.example.codersfree.dto.PriceDto;
import com.example.codersfree.model.Price;
import com.example.codersfree.repository.PriceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Price> paginate(Pageable pageable) {
        return priceRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Price findById(Long id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Precio no encontrado con id: " + id));
    }

    @Transactional
    public Price save(PriceDto priceDto) {
        Price price = Price.builder()
                .name(priceDto.getName())
                .value(priceDto.getValue())
                .build();
        return priceRepository.save(price);
    }

    @Transactional
    public Price update(Long id, PriceDto priceDto) {
        Price price = findById(id);
        
        price.setName(priceDto.getName());
        price.setValue(priceDto.getValue());
        
        return priceRepository.save(price);
    }

    @Transactional
    public void delete(Long id) {
        Price price = findById(id);
        priceRepository.delete(price);
    }
}