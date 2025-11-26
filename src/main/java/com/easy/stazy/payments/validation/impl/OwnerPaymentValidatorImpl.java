package com.easy.stazy.payments.validation.impl;

import com.easy.stazy.payments.validation.OwnerPaymentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OwnerPaymentValidatorImpl implements OwnerPaymentValidator {
    private static final Logger logger = LoggerFactory.getLogger(OwnerPaymentValidatorImpl.class);

    public void validateRentalOptionExists(Object amountToPay, Long tenantId) {
        if (amountToPay == null) {
            logger.error("[validateRentalOptionExists] No rental option found for tenant's room and sharing type (tenantId: {})", tenantId);
            throw new IllegalStateException("No rental option found for tenant's room and sharing type");
        }
    }
}

