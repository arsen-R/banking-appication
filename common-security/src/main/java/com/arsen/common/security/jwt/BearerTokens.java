package com.arsen.common.security.jwt;

public final class BearerTokens {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private BearerTokens() {
    }

    public static boolean hasBearerToken(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX);
    }

    public static String extractToken(String authorizationHeader) {
        return hasBearerToken(authorizationHeader)
                ? authorizationHeader.substring(BEARER_PREFIX.length())
                : null;
    }
}
