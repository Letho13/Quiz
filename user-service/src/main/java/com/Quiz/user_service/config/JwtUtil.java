package com.Quiz.user_service.config;

import com.Quiz.user_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {


    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private long expirationTime;

    private SecretKey secretKey;

    /**
     * Initialise la clé secrète à partir de la chaîne encodée.
     * Appelée automatiquement après l'injection des propriétés.
     */
    @PostConstruct
    public void init() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("La clé JWT doit faire au moins 32 caractères !");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Génère un token JWT signé pour un utilisateur donné.
     *
     * @param username le nom d'utilisateur à inclure dans le token
     * @return le token JWT signé
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Vérifie la validité d’un token JWT.
     *
     * @param token le token à valider
     * @return {@code true} si le token est valide, sinon {@code false}
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            System.err.println("[validateToken] Exception: " + e.getMessage());
            return false;
        }
    }


    /**
     * Extrait le username d’un token JWT.
     *
     * @param token le token JWT
     * @return le username contenu dans le token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}


