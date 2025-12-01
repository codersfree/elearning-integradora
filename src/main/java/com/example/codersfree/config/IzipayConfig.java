package com.example.codersfree.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "izipay")
@Getter
@Setter
public class IzipayConfig {
    private String url;
    private String clientId;
    private String clientSecret;
    private String publicKey;
    private String hashKey;
}