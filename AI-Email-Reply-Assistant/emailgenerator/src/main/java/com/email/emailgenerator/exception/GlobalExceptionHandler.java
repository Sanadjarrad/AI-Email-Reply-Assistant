package com.email.emailgenerator.exception;

import com.email.emailgenerator.exception.custom.ExternalApiException;
import com.email.emailgenerator.exception.custom.ResponseGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseGenerationException.class)
    public ResponseEntity<Map<String, String>> handleEmailGenerationException(ResponseGenerationException re) {
        log.error("Email generation error: {}", re.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", "Email generation failed");
        response.put("message", re.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Map<String, String>> handleExternalApiException(ExternalApiException ex) {
        log.error("External API error: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", "External API failure");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        log.error("Validation error: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
