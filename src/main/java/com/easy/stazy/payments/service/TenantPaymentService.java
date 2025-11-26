package com.easy.stazy.payments.service;

import com.easy.stazy.payments.dto.request.TenantPaymentRequestDto;
import com.easy.stazy.payments.dto.response.TenantRentPaymentsDto;
import com.easy.stazy.payments.dto.response.TenantRentsResponseDto;
import com.easy.stazy.payments.enums.PaymentStatus;
import com.easy.stazy.payments.entity.TenantPaymentEntity;

import java.util.List;

public interface TenantPaymentService {
    void savePayment(TenantPaymentRequestDto dto);

    List<TenantRentPaymentsDto> getPaymentsByTenant(Long tenantId);

    TenantPaymentEntity getPaymentById(Long id);

    TenantPaymentEntity updatePaymentStatus(Long paymentId, PaymentStatus status);

    void markPaymentAsPaidByOwner(Long tenantId, int month, int year);

    List<TenantRentsResponseDto> getTenantRentsSummary(Long tenantId);
}
