package com.easy.stazy.shared.common.exception;

import java.io.Serial;

public class ExternalServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6131223246458706312L;

    public ExternalServiceException(String message) {
        super(message);
    }
    public ExternalServiceException(String message, Exception e){
        super(message, e);
    }
}
