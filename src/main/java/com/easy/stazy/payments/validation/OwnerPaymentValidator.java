package com.easy.stazy.payments.validation;

public interface OwnerPaymentValidator {
    void validateRentalOptionExists(Object amountToPay, Long tenantId);
}
