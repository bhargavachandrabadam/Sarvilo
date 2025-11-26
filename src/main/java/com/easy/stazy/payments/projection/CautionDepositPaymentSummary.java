package com.easy.stazy.payments.projection;

import java.math.BigDecimal;

public interface CautionDepositPaymentSummary {
    String getStatus();
    BigDecimal getAmountPaid();
    Long getBookingRequestId();
}

