package com.ipin.identity.exception;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ipin.identity.dto.response.ApiResponse;

import jakarta.validation.ConstraintViolation;

@ControllerAdvice
public class GlobalHandlingException {
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<?>> handlingAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String enumKey = e.getFieldError().getDefaultMessage();
        
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        Map<String, Object> mapAttrs = Map.of();
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation = e.getBindingResult()
                .getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            mapAttrs = constraintViolation.getConstraintDescriptor().getAttributes();
        }
        catch(IllegalArgumentException ex) {
            
        }

        return ResponseEntity.badRequest().body(ApiResponse.builder()
            .code(errorCode.getCode())
            .message(Objects.nonNull(mapAttrs) 
                ? errorCode.getMessage().replace("{min}" , String.valueOf(mapAttrs.get("min")))
                : errorCode.getMessage())
            .build());
    }
}
