package com.arsen.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GatewayConfigTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void routesAuthPathsToAuthServiceThroughJwtFilter() {
        RouteLocator routeLocator = applicationContext.getBean("routeLocator", RouteLocator.class);

        List<Route> routes = routeLocator.getRoutes().collectList().block();

        assertThat(routes).hasSize(1);
        Route route = routes.getFirst();
        assertThat(route.getId()).isEqualTo("auth-service");
        assertThat(route.getUri()).isEqualTo(URI.create("lb://auth-service"));
        assertThat(route.getFilters()).hasSize(1);
        assertThat(matches(route, "/api/v1/auth/login")).isTrue();
        assertThat(matches(route, "/api/v1/accounts")).isFalse();
    }

    private boolean matches(Route route, String path) {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
        return Boolean.TRUE.equals(Mono.from(route.getPredicate().apply(exchange)).block());
    }
}
