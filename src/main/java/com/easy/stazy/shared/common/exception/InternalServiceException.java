package com.easy.stazy.shared.common.exception;

import java.io.Serial;

public class InternalServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5467824262217787294L;

    public InternalServiceException(String message) {
        super(message);
    }
}
