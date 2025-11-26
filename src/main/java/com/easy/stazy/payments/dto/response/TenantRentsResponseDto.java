package com.easy.stazy.payments.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantRentsResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -3576314429816764302L;

    private int month;

    private int year;

    private BigDecimal amount;

    private BigDecimal amountPaid;

    private String status;
}
