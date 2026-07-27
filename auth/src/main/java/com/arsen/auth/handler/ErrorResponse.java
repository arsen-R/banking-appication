package com.arsen.auth.handler;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String path;
    private HttpStatus httpStatus;
    private Instant timestamp;
}
