package com.easy.stazy.pgmanagement.owner.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InAppPaymentsRequiredValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InAppPaymentsRequired {
    String message() default "In-app payment details are mandatory when inAppPayments is true";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
