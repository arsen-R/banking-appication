package com.arsen.apigateway.filter;

import com.arsen.apigateway.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public static class Config {
        private List<String> publicEndpoints = List.of();

        public List<String> getPublicEndpoints() {
            return publicEndpoints;
        }

        public Config setPublicEndpoints(List<String> publicEndpoints) {
            this.publicEndpoints = publicEndpoints == null ? List.of() : List.copyOf(publicEndpoints);
            return this;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (config != null && config.getPublicEndpoints().stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                log.warn("Missing or malformed Authorization header for path: {}", path);
                return writeError(exchange, HttpStatus.UNAUTHORIZED, "Missing or malformed Authorization header");
            }

            String jwtToken = authHeader.substring(BEARER_PREFIX.length()).trim();
            if (jwtToken.isEmpty()) {
                log.warn("Empty bearer token for path: {}", path);
                return writeError(exchange, HttpStatus.UNAUTHORIZED, "Missing or malformed Authorization header");
            }

            try {
                jwtUtil.validateToken(jwtToken);
                log.debug("Token validation succeeded for path: {}", path);
                return chain.filter(exchange);
            } catch (ExpiredJwtException e) {
                log.warn("Expired token for path {}: {}", path, e.getMessage());
                return writeError(exchange, HttpStatus.UNAUTHORIZED, "Token expired");
            } catch (JwtException | IllegalArgumentException e) {
                log.warn("Invalid token for path {}: {}", path, e.getMessage());
                return writeError(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");
            } catch (RuntimeException e) {
                log.error("Unexpected error while validating token for path: {}", path, e);
                return writeError(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
            }
        };
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String detail) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        String body = """
                {"status":%d,"title":"%s","detail":"%s"}"""
                .formatted(status.value(), status.getReasonPhrase(), detail);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
