package com.easy.stazy.shared.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static com.easy.stazy.shared.common.constants.MessageConstants.ALREADY_EXISTS;
import static java.lang.String.format;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 635330670076571153L;

    public AlreadyExistsException(String resource, String field, Object value) {
        super(format(ALREADY_EXISTS, resource, field, value));
    }
    public AlreadyExistsException(String message) {
        super(message);
    }
}
