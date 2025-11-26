package com.easy.stazy.shared.common.exception;

import lombok.Getter;

import java.io.Serial;
import java.util.Map;

@Getter
public class ValidationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 534538236503511294L;
    private final Map<String, String> errors;

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}
