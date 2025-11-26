package com.easy.stazy.payments.dto.request;

import com.easy.stazy.payments.enums.PaymentMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TenantPaymentRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1283595631534207848L;
    @NotNull
    private Long tenantId;
    @NotNull
    private Integer month;
    @NotNull
    private Integer year;
    @NotNull
    private Long pgId;
    @NotNull
    @Positive
    private Double amount;
    @NotNull
    private PaymentMode mode; // Use enum instead of String
    private String transactionRef; // required if mode is ONLINE
}
