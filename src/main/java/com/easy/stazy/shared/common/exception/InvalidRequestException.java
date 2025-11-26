package com.easy.stazy.shared.common.exception;

import java.io.Serial;

public class InvalidRequestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1578265209790056700L;

    public InvalidRequestException(String message) {
        super(message);
    }
}
