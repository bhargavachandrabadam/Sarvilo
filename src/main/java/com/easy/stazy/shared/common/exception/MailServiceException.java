package com.easy.stazy.shared.common.exception;

import java.io.Serial;

public class MailServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -7435092023041106755L;

    public MailServiceException(String message, Throwable cause) {
        super(message,cause);
    }
}
