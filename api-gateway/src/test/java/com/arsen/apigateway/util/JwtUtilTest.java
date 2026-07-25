package com.arsen.apigateway.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {
    private static final String SECRET_KEY = "5b4229ea4e3886d9fddafe27d0e6467f139970b0acb52701cb79d30823215027";
    private static final String OTHER_SECRET_KEY = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", SECRET_KEY);
    }

    @Test
    void validateTokenReturnsSubject() {
        String token = token(SECRET_KEY, new Date(System.currentTimeMillis() + 60_000));

        assertThat(jwtUtil.validateToken(token)).isEqualTo("john");
    }

    @Test
    void validateTokenRejectsExpiredToken() {
        String token = token(SECRET_KEY, new Date(System.currentTimeMillis() - 60_000));

        assertThatThrownBy(() -> jwtUtil.validateToken(token)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void validateTokenRejectsTokenSignedWithAnotherKey() {
        String token = token(OTHER_SECRET_KEY, new Date(System.currentTimeMillis() + 60_000));

        assertThatThrownBy(() -> jwtUtil.validateToken(token)).isInstanceOf(SignatureException.class);
    }

    @Test
    void validateTokenRejectsMalformedToken() {
        assertThatThrownBy(() -> jwtUtil.validateToken("not-a-jwt")).isInstanceOf(RuntimeException.class);
    }

    private String token(String key, Date expiration) {
        return Jwts.builder()
                .subject("john")
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(secretKey(key))
                .compact();
    }

    private SecretKey secretKey(String key) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }
}
