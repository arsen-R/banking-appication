package com.arsen.apigateway.util;

import com.arsen.common.security.jwt.JwtTokenParser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtTokenParser jwtTokenParser;

    public String validateToken(final String token) {
        Claims claims = jwtTokenParser.extractAllClaims(token);
        if (claims.getExpiration().before(new Date())) {
            throw new IllegalStateException("Token expired");
        }
        return claims.getSubject();
    }
}
