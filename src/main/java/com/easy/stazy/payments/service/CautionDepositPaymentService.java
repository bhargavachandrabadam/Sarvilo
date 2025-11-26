package com.easy.stazy.payments.service;

import com.easy.stazy.payments.dto.request.CautionDepositPaymentRequestDto;

public interface CautionDepositPaymentService {
    void saveCautionDepositPayment(CautionDepositPaymentRequestDto dto);
}

