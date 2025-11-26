package com.easy.stazy.payments.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CautionDepositPaymentRequestDto {
    private Long bookingId;
    private Long userId;
    private BigDecimal amountPaid;
    private String mode; // CASH or ONLINE
    private String transactionRef; // required if mode is ONLINE
}

