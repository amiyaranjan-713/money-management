package com.qsp.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.qsp.entity.ProfileEntity;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final String SECRET_KEY;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        this.SECRET_KEY = secretKey;
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(ProfileEntity profile) {

        return Jwts.builder()
                .subject(profile.getEmail())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + (1000 * 60 * 60))
                )
                .signWith(getKey())
                .compact();
    }

    public String extractUserName(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        try {
            String username = extractUserName(token);

            return username.equals(userDetails.getUsername());

        } catch (Exception e) {
            return false;
        }
    }
}