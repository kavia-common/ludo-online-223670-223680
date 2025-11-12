package com.example.backend.controller;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * Global exception handler mapping exceptions to consistent responses.
 *
 * PUBLIC_INTERFACE
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    record ErrorBody(String code, String message, String requestId, Instant timestamp) {}

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorBody> notFound(NoSuchElementException ex) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder("Validation failed");
        if (!ex.getBindingResult().getAllErrors().isEmpty()) {
            var e = (FieldError) ex.getBindingResult().getAllErrors().get(0);
            sb.append(": ").append(e.getField()).append(" ").append(e.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", new IllegalArgumentException(sb.toString()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorBody> badRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorBody> conflict(IllegalStateException ex) {
        return build(HttpStatus.CONFLICT, "CONFLICT", ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> generic(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex);
    }

    @MessageExceptionHandler
    public void wsErrors(Exception ex) {
        // Log WebSocket errors to help debugging
        log.warn("WS error: {}", ex.getMessage());
    }

    private ResponseEntity<ErrorBody> build(HttpStatus status, String code, Exception ex) {
        String reqId = UUID.randomUUID().toString();
        log.warn("error status={} code={} requestId={} msg={}", status.value(), code, reqId, ex.getMessage());
        return ResponseEntity.status(status)
                .body(new ErrorBody(code, ex.getMessage(), reqId, Instant.now()));
        }
}
