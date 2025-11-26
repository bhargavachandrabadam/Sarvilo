package com.easy.stazy.pgmanagement.owner.validation;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InAppPaymentsRequiredValidator implements ConstraintValidator<InAppPaymentsRequired, PgManagementRequestDto> {

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(PgManagementRequestDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Boolean inApp = value.getInAppPayments();
        if (Boolean.TRUE.equals(inApp)) {
            boolean ok = isNotBlank(value.getUpiAddress())
                    && isNotBlank(value.getGstin())
                    && isNotBlank((value.getBankAccountNumber()))
                    && isNotBlank((value.getAccountHolderName()))
                    && isNotBlank(value.getIfsc());
            if (!ok) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("All in-app payments fields (upiAddress, gstin, bankAccountNUmber, accountHolderName, ifsc) are required when inAppPayments is true")
                        .addConstraintViolation();
            }
            return ok;
        }
        return true;
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
