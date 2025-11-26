package com.easy.stazy.payments.validation;

import com.easy.stazy.payments.dto.request.TenantPaymentRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;

import java.math.BigDecimal;

public interface TenantPaymentValidator {
    void validatePaymentRequest(TenantPaymentRequestDto dto);
    void validateTenantExists(TenantInfoEntity tenant, Long tenantId);
    void validateActiveOccupancy(BedOccupancyEntity occupancy, Long tenantId);
    void validatePaymentExists(Object payment, Long paymentId);
    void validateAmountNotExceeding(BigDecimal amountPaid, BigDecimal actualAmount);
}
