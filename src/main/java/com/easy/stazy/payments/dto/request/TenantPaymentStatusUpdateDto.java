package com.easy.stazy.payments.dto.request;

import com.easy.stazy.payments.enums.PaymentStatus;
import lombok.Data;

@Data
public class TenantPaymentStatusUpdateDto {
    private PaymentStatus status;
}

