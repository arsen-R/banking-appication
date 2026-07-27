package com.arsen.apigateway.filter;

import com.arsen.apigateway.util.JwtUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private final JwtUtil jwtUtil;

    public static class Config {
        private List<String> publicEndpoints;

        public List<String> getPublicEndpoints() {
            return publicEndpoints;
        }

        public Config setPublicEndpoints(List<String> publicEndpoints) {
            this.publicEndpoints = publicEndpoints;
            return this;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (config != null && config.getPublicEndpoints() != null
                    && config.getPublicEndpoints().stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or malformed Authorization header for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String jwtToken = authHeader.substring(7);
            try {
                jwtUtil.validateToken(jwtToken);
                log.debug("Token validation succeeded for path: {}", path);
                return chain.filter(exchange);
            } catch (FeignException.Unauthorized e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                log.error("(Feign Exception - Unauthorized: {}", path, e);
                return exchange.getResponse().setComplete();
            } catch (FeignException.Forbidden e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                log.error("(Feign Exception - Forbidden: {}", path, e);
                return exchange.getResponse().setComplete();
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                log.error("Token validation failed for path: {}", path, e);
                return exchange.getResponse().setComplete();
            }
        };
    }
}
