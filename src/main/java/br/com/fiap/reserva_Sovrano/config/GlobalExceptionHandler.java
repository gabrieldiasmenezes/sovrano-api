package br.com.fiap.reserva_Sovrano.config;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse(400, ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(errorResponse(409, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse(500, "Erro inesperado: " + ex.getMessage()));
    }

    private Map<String, Object> errorResponse(int status, String message) {
        return Map.of(
            "timestamp", LocalDateTime.now(),
            "status", status,
            "error", message
        );
    }
}

