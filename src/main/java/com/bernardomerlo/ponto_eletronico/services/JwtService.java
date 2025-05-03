package com.bernardomerlo.ponto_eletronico.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final Key secretKey = Keys.hmacShaKeyFor("bernardoantoniomerlosoares123arrobagmailpontocompontobr".getBytes());

    public String generateToken(Long id, String email, String name) {
        long expirationTime = 1000 * 60 * 20;
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("email", email)
                .claim("name", name)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
