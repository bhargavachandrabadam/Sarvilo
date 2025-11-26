package com.easy.stazy.payments.repository;

import com.easy.stazy.payments.entity.CautionDepositPaymentEntity;
import com.easy.stazy.payments.projection.CautionDepositPaymentSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CautionDepositPaymentRepository extends JpaRepository<CautionDepositPaymentEntity, Long> {
    List<CautionDepositPaymentEntity> findByBookingRequestId(Long bookingId);
    List<CautionDepositPaymentEntity> findByBookingRequestIdIn(List<Long> bookingIds);
    List<CautionDepositPaymentEntity> findByUserId(Long userId);
    List<CautionDepositPaymentSummary> findSummaryByBookingRequestIdIn(List<Long> bookingIds);
}
