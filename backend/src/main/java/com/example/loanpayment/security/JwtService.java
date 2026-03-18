package com.example.loanpayment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final JwtProperties props;
    private final Key key;

    public JwtService(JwtProperties props) {
        this.props = props;
        String secret = System.getenv().getOrDefault("JWT_SECRET", "b7e3f1c9a6d4e2f8b5c1a9d7e3f6b2c8a4d1e9f7c6b3a5d8f2e1c9b7a4d6f3");
        // HS256 requires >= 256-bit key. Ensure length.
        if (secret.length() < 32) {
            secret = (secret + "00000000000000000000000000000000").substring(0, 32);
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, List<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getExpirationMinutes() * 60);

        return Jwts.builder()
                .setIssuer(props.getIssuer())
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(props.getIssuer())
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
