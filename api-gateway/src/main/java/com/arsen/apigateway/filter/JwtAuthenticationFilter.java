package com.arsen.apigateway.filter;

import com.arsen.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    private final JwtUtil  jwtUtil;
    private static final List<String> PUBLIC_ENDPOINTS = List.of("/v1/auth/login", "/v1/auth/register", "/eureka");

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        Predicate<ServerRequest> serverRequestPredicate = r -> PUBLIC_ENDPOINTS.stream()
                .noneMatch(uri -> r.uri().getPath().contains(uri));

        if (serverRequestPredicate.test(request)) {
            if (authMissing(request)) {
                return onError();
            }

            String token = request.headers().asHttpHeaders().getFirst("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            try {
                jwtUtil.validateToken(token);
            } catch (Exception e) {
                return onError();
            }
        }

        return next.handle(request);
    }

    private ServerResponse onError() {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }

    private boolean authMissing(ServerRequest request) {
        return request.headers().asHttpHeaders().getFirst("Authorization") == null;
    }
}
