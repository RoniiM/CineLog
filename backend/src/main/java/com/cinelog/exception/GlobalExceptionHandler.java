package com.cinelog.exception;

import com.cinelog.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMovieNotFound(MovieNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(ExternalApiException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(Instant.now(), status.value(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
