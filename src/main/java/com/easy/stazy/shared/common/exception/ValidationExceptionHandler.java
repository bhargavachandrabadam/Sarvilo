package com.easy.stazy.shared.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    /**
     * Handle validation exception.
     * <p>
     * This method is used to handle {@link MethodArgumentNotValidException}
     * exceptions that occur during validation of method arguments.
     * <p>
     * It returns a {@link ResponseEntity} object containing the errors.
     *
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity containing errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(err -> {
                    if (err instanceof FieldError fe) {
                        return fe.getField() + ": " + fe.getDefaultMessage();
                    }
                    return err.getDefaultMessage();
                })
                .toList();
        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }
}
