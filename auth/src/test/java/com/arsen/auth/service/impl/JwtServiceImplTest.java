package com.arsen.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceImplTest {
    private static final String SECRET_KEY = "5b4229ea4e3886d9fddafe27d0e6467f139970b0acb52701cb79d30823215027";
    private static final String OTHER_SECRET_KEY = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
    private static final long EXPIRATION = 900_000L;

    private JwtServiceImpl jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "JWT_SECRET_KEY", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "JWT_EXPIRATION", EXPIRATION);
        userDetails = User.withUsername("john")
                .password("secret")
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();
    }

    @Test
    void generateTokenPutsUserDetailsIntoClaims() {
        String token = jwtService.generateToken(userDetails);

        Claims claims = parse(token, SECRET_KEY);
        assertThat(claims.getSubject()).isEqualTo("john");
        assertThat(claims.get("username")).isEqualTo("john");
        assertThat(claims.get("isEnabled")).isEqualTo(true);
        assertThat(claims.get("isAccountNonLocked")).isEqualTo(true);
        assertThat(claims.get("roles", List.class)).hasSize(1);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void generateTokenUsesConfiguredExpiration() {
        long before = System.currentTimeMillis();

        Date expiration = jwtService.extractExpiration(jwtService.generateToken(userDetails));

        assertThat(expiration.getTime()).isBetween(before + EXPIRATION, System.currentTimeMillis() + EXPIRATION);
    }

    @Test
    void extractUsernameReturnsSubject() {
        assertThat(jwtService.extractUsername(jwtService.generateToken(userDetails))).isEqualTo("john");
    }

    @Test
    void extractClaimAppliesResolver() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractClaim(token, claims -> claims.get("username", String.class));

        assertThat(username).isEqualTo("john");
    }

    @Test
    void validateTokenReturnsTrueForMatchingUser() {
        assertThat(jwtService.validateToken(jwtService.generateToken(userDetails), userDetails)).isTrue();
    }

    @Test
    void validateTokenReturnsFalseForAnotherUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails other = User.withUsername("jane").password("secret").authorities("ROLE_USER").build();

        assertThat(jwtService.validateToken(token, other)).isFalse();
    }

    @Test
    void validateTokenRejectsExpiredToken() {
        String token = expiredToken();

        assertThatThrownBy(() -> jwtService.validateToken(token, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void extractUsernameRejectsTokenSignedWithAnotherKey() {
        ReflectionTestUtils.setField(jwtService, "JWT_SECRET_KEY", OTHER_SECRET_KEY);
        String foreignToken = Jwts.builder()
                .subject("john")
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey(SECRET_KEY))
                .compact();

        assertThatThrownBy(() -> jwtService.extractUsername(foreignToken))
                .isInstanceOf(SignatureException.class);
    }

    private String expiredToken() {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject("john")
                .issuedAt(new Date(now - 2 * EXPIRATION))
                .expiration(new Date(now - EXPIRATION))
                .signWith(secretKey(SECRET_KEY))
                .compact();
    }

    private Claims parse(String token, String key) {
        return Jwts.parser().verifyWith(secretKey(key)).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey secretKey(String key) {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
    }
}
