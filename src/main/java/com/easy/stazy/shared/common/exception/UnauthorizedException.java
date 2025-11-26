package com.easy.stazy.shared.common.exception;

import java.io.Serial;

public class UnauthorizedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6407747631179203017L;

    public UnauthorizedException(String message) {
        super(message);
    }
}
