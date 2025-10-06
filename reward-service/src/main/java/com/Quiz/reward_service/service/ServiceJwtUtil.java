package com.Quiz.reward_service.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class ServiceJwtUtil {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private long expirationTime;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Génère un JWT pour les appels inter-services avec l'autorité GATEWAY_CALL.
     */
    public String generateGatewayToken() {
        return Jwts.builder()
                .subject("REWARD_SERVICE")       // Nom arbitraire pour le service
                .claim("role", "SERVICE")        // Rôle arbitraire
                .claim("authorities", List.of("GATEWAY_CALL"))  // Autorité spéciale
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }


}