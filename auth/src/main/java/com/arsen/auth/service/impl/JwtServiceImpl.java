package com.arsen.auth.service.impl;

import com.arsen.auth.service.JwtService;
import com.arsen.common.security.jwt.JwtProperties;
import com.arsen.common.security.jwt.JwtTokenParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtTokenParser jwtTokenParser;
    private final JwtProperties jwtProperties;

    private static final String ROLES = "roles";
    private static final String USERNAME = "username";
    private static final String IS_ENABLED = "isEnabled";
    private static final String IS_ACCOUNT_NON_LOCKED = "isAccountNonLocked";

    @Override
    public String extractUsername(String token) {
        return jwtTokenParser.extractUsername(token);
    }

    @Override
    public Date extractExpiration(String token) {
        return jwtTokenParser.extractExpiration(token);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return jwtTokenParser.extractClaim(token, claimsResolver);
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !jwtTokenParser.isTokenExpired(token);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails);
    }

    private String createToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)
                .claim(ROLES, userDetails.getAuthorities())
                .claim(USERNAME, userDetails.getUsername())
                .claim(IS_ENABLED, userDetails.isEnabled())
                .claim(IS_ACCOUNT_NON_LOCKED, userDetails.isAccountNonLocked())
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(jwtTokenParser.getSecretKey())
                .compact();
    }
}
