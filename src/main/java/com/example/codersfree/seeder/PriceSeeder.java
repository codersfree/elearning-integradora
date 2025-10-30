package com.example.codersfree.seeder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.codersfree.model.Price;
import com.example.codersfree.repository.PriceRepository;

@Component
public class PriceSeeder implements Seeder {

    @Autowired
    private PriceRepository priceRepository;

    @Override
    public void seed() {

        System.out.println("Sembrando precios...");

        Price price1 = Price.builder().name("Gratis").value(new BigDecimal("0.00")).build();
        Price price2 = Price.builder().name("USD 9.99").value(new BigDecimal("9.99")).build();
        Price price3 = Price.builder().name("USD 19.99").value(new BigDecimal("19.99")).build();
        Price price4 = Price.builder().name("USD 49.99").value(new BigDecimal("49.99")).build();
        List<Price> prices = Arrays.asList(price1, price2, price3, price4);

        priceRepository.saveAll(prices);
    }
}
