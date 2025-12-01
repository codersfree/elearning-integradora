package com.example.codersfree.service;

import com.example.codersfree.config.IzipayConfig;
import com.example.codersfree.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class IzipayService {

    @Autowired
    private IzipayConfig izipayConfig;

    @Autowired
    private CartService cartService;

    /**
     * Genera el formToken comunicándose con la API de Izipay.
     */
    public String generateFormToken(User user) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Configurar Headers (Basic Auth)
        String auth = izipayConfig.getClientId() + ":" + izipayConfig.getClientSecret();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");

        // 2. Preparar el Body
        // IMPORTANTE: Izipay trabaja en CENTAVOS.
        // Si el total es 9.99, enviamos 999.
        long amountInCents = cartService.getTotal().multiply(new BigDecimal(100)).longValue();

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amountInCents);
        
        // --- CORRECCIÓN: CAMBIADO A PEN ---
        // Tu cuenta de pruebas de Izipay parece estar configurada solo para Soles.
        body.put("currency", "PEN"); 
        // ----------------------------------
        
        body.put("orderId", UUID.randomUUID().toString().substring(0, 20)); // ID único de orden
        
        // Datos del cliente
        Map<String, String> customer = new HashMap<>();
        customer.put("email", user.getEmail());
        customer.put("reference", String.valueOf(user.getId()));
        body.put("customer", customer);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            String endpoint = izipayConfig.getUrl() + "/Charge/CreatePayment";
            
            // --- DEBUG LOGS (Ver en consola) ---
            System.out.println(">>> IZIPAY REQUEST URL: " + endpoint);
            System.out.println(">>> IZIPAY BODY: " + body);
            // -----------------------------------

            // Hacemos la petición esperando un String para poder imprimir la respuesta cruda
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);

            // --- DEBUG LOGS ---
            System.out.println(">>> IZIPAY RESPONSE RAW: " + response.getBody());
            // ------------------

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Parseamos el JSON
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode answer = root.path("answer");

                // Verificamos si existe el formToken
                if (answer.has("formToken")) {
                    return answer.path("formToken").asText();
                } else {
                    // Si no hay token, buscamos el mensaje de error de Izipay
                    String errorMsg = answer.path("errorMessage").asText();
                    String errorCode = answer.path("errorCode").asText();
                    System.err.println(">>> ERROR IZIPAY: " + errorCode + " - " + errorMsg);
                    throw new RuntimeException("Error Izipay: " + errorMsg);
                }
            } else {
                throw new RuntimeException("Izipay respondió con estado HTTP: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            // Capturar errores 4xx (como 401 Unauthorized o 400 Bad Request)
            System.err.println(">>> ERROR HTTP CLIENTE: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error de cliente Izipay: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error general generando token: " + e.getMessage());
        }
    }

    /**
     * Valida la firma HMAC SHA256.
     * Replica la lógica de Laravel: checkHash()
     */
    public boolean validateHash(Map<String, String> params) {
        String krHash = params.get("kr-hash");
        String krAnswer = params.get("kr-answer");
        String krHashAlgorithm = params.get("kr-hash-algorithm");

        // Validar algoritmo
        if (!"sha256_hmac".equals(krHashAlgorithm)) {
            return false;
        }

        // Replica lógica PHP: $kr_answer = str_replace('\/', '/', $data['kr-answer']);
        // Esto es necesario porque a veces el JSON viene con slashes escapados
        String textToHash = krAnswer.replace("\\/", "/");

        // Calcular HMAC
        String calculatedHash = calculateHmacSha256(textToHash, izipayConfig.getHashKey());

        return calculatedHash.equals(krHash);
    }

    private String calculateHmacSha256(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Convertir bytes a Hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : rawHmac) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculando HMAC", e);
        }
    }
}