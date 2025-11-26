package com.easy.stazy.shared.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static com.easy.stazy.shared.common.constants.MessageConstants.NOT_FOUND;
import static java.lang.String.format;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -3798516296148442143L;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(format(NOT_FOUND, resourceName, fieldName, fieldValue));
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
