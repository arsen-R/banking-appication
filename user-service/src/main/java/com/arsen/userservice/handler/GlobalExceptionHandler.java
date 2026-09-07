package com.arsen.userservice.handler;

import com.arsen.userservice.exception.UserNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    protected ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                         @NonNull HttpHeaders headers,
                                                         @NonNull HttpStatus status,
                                                         @NonNull WebRequest request) {
        log.error(ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(x -> errors.put(((FieldError) x).getField(), x.getDefaultMessage()));
        return ErrorResponse.builder()
                .error(MethodArgumentNotValidException.class.getSimpleName())
                .message(ex.getMessage())
                .status(status.value())
                .timestamp(Instant.now())
                .details(errors)
                .path(request.getContextPath())
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerMethodUserNotFoundException(UserNotFoundException e) {
        log.error(UserNotFoundException.class.getSimpleName(), e.getMessage());
        return ErrorResponse.builder()
                .error(UserNotFoundException.class.getSimpleName())
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(Instant.now())
                .build();
    }


}
