package com.arsen.apigateway.filter;

import com.arsen.apigateway.util.JwtUtil;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private static final List<String> PUBLIC_ENDPOINTS = List.of("/api/v1/auth/login", "/api/v1/auth/register");

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private GatewayFilterChain chain;

    @Test
    void shortCircuitsPublicEndpointWithoutValidatingToken() {
        MockServerWebExchange exchange = exchange("/api/v1/auth/login");

        filter().filter(exchange, chain).block();

        verify(jwtUtil, never()).validateToken(any());
        verify(chain, never()).filter(any());
        assertThat(exchange.getResponse().isCommitted()).isTrue();
    }

    @Test
    void delegatesToChainWhenTokenIsValid() {
        MockServerWebExchange exchange = exchange("/api/v1/accounts");
        when(jwtUtil.validateToken("/accounts")).thenReturn("john");
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        filter().filter(exchange, chain).block();

        verify(chain).filter(exchange);
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void returnsUnauthorizedOnFeignUnauthorized() {
        MockServerWebExchange exchange = exchange("/api/v1/accounts");
        when(jwtUtil.validateToken("/accounts")).thenThrow(feignException(401));

        filter().filter(exchange, chain).block();

        verify(chain, never()).filter(any());
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void returnsUnauthorizedOnFeignForbidden() {
        MockServerWebExchange exchange = exchange("/api/v1/accounts");
        when(jwtUtil.validateToken("/accounts")).thenThrow(feignException(403));

        filter().filter(exchange, chain).block();

        verify(chain, never()).filter(any());
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void returnsServerErrorWhenTokenValidationFails() {
        MockServerWebExchange exchange = exchange("/api/v1/accounts");
        when(jwtUtil.validateToken("/accounts")).thenThrow(new SignatureException("bad signature"));

        filter().filter(exchange, chain).block();

        verify(chain, never()).filter(any());
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private GatewayFilter filter() {
        JwtAuthenticationFilter.Config config = new JwtAuthenticationFilter.Config().setPublicEndpoints(PUBLIC_ENDPOINTS);
        return new JwtAuthenticationFilter(jwtUtil).apply(config);
    }

    private MockServerWebExchange exchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }

    private FeignException feignException(int status) {
        Request request = Request.create(Request.HttpMethod.GET, "http://auth-service", Map.of(), null,
                StandardCharsets.UTF_8, new RequestTemplate());
        return FeignException.errorStatus("validateToken", feign.Response.builder()
                .status(status)
                .reason("error")
                .request(request)
                .headers(Collections.emptyMap())
                .build());
    }
}
