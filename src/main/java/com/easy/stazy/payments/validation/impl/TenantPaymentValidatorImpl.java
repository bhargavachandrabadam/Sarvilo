package com.easy.stazy.payments.validation.impl;

import com.easy.stazy.payments.dto.request.TenantPaymentRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.payments.validation.TenantPaymentValidator;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class TenantPaymentValidatorImpl implements TenantPaymentValidator {
    private static final Logger logger = LoggerFactory.getLogger(TenantPaymentValidatorImpl.class);

    @Override
    public void validatePaymentRequest(TenantPaymentRequestDto dto) {
        if (dto.getMode() != null && dto.getMode().name().equalsIgnoreCase("ONLINE") && dto.getTransactionRef() == null) {
            logger.warn("[validatePaymentRequest] Transaction reference is required for ONLINE payments.");
            throw new IllegalArgumentException("Transaction reference is required for ONLINE payments.");
        }
    }

    @Override
    public void validateAmountNotExceeding(BigDecimal amountPaid, BigDecimal actualAmount) {
        if (amountPaid != null && actualAmount != null && amountPaid.compareTo(actualAmount) > 0) {
            logger.error("[validateAmountNotExceeding] Amount paid ({}) exceeds actual amount ({}).", amountPaid, actualAmount);
            throw new IllegalArgumentException("Amount paid cannot exceed the actual amount due.");
        }
    }

    @Override
    public void validateTenantExists(TenantInfoEntity tenant, Long tenantId) {
        if (tenant == null) {
            logger.error("[validateTenantExists] Tenant not found with id: {}", tenantId);
            throw new ResourceNotFoundException("Tenant not found with id: " + tenantId);
        }
    }

    @Override
    public void validateActiveOccupancy(BedOccupancyEntity occupancy, Long tenantId) {
        if (occupancy == null) {
            logger.error("[validateActiveOccupancy] No active bed occupancy found for tenant id: {}", tenantId);
            throw new ResourceNotFoundException("No active bed occupancy found for tenant id: " + tenantId);
        }
    }

    @Override
    public void validatePaymentExists(Object payment, Long paymentId) {
        if (payment == null) {
            logger.error("[validatePaymentExists] Payment not found with id: {}", paymentId);
            throw new ResourceNotFoundException("Payment not found with id: " + paymentId);
        }
    }
}
