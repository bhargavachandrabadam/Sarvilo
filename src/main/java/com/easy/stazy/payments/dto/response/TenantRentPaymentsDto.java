package com.easy.stazy.payments.dto.response;

import com.easy.stazy.payments.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantRentPaymentsDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 2210678393426067812L;
    private Integer month;
    private Integer year;
    private BigDecimal amount;
    private PaymentStatus status;
}

