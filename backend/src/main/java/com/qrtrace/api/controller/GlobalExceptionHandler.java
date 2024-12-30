package com.qrtrace.api.controller;

import com.qrtrace.api.model.ErrorResponse;
import com.qrtrace.api.exception.QrCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(QrCodeException.class)
    public ResponseEntity<ErrorResponse> handleQrCodeException(QrCodeException e) {
        log.error("QR Code error: ", e);
        ErrorResponse error = new ErrorResponse(e.getMessage());
        
        if (e.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } else if (e.getMessage().contains("not authenticated")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } else if (e.getMessage().contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } else if (e.getMessage().contains("Invalid")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error: ", e);
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse("입력값이 올바르지 않습니다: " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error: ", e);
        ErrorResponse error = new ErrorResponse("서버 내부 오류가 발생했습니다");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
} 