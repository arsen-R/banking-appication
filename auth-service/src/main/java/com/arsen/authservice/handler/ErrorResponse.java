package com.arsen.authservice.handler;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;

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
