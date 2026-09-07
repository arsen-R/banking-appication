package com.arsen.userservice.handler;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private Integer status;
    private Map<String, String> details;
    private Instant timestamp;
    private String path;
}