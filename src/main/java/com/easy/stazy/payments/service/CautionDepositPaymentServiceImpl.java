package com.easy.stazy.payments.service;

import com.easy.stazy.payments.dto.request.CautionDepositPaymentRequestDto;
import com.easy.stazy.bookings.entity.BookingRequestEntity;
import com.easy.stazy.payments.entity.CautionDepositPaymentEntity;
import com.easy.stazy.payments.enums.PaymentStatus;
import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
import com.easy.stazy.pgmanagement.pg.enums.RentalType;
import com.easy.stazy.bookings.repository.BookingRequestRepository;
import com.easy.stazy.payments.repository.CautionDepositPaymentRepository;
import com.easy.stazy.pgmanagement.pg.repository.RentalOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CautionDepositPaymentServiceImpl implements CautionDepositPaymentService {

    private final CautionDepositPaymentRepository cautionDepositPaymentRepository;

    private final BookingRequestRepository bookingRequestRepository;

    private final RentalOptionRepository rentalOptionRepository;

    @Transactional
    @Override
    public void saveCautionDepositPayment(CautionDepositPaymentRequestDto dto) {
        CautionDepositPaymentEntity payment = new CautionDepositPaymentEntity();
        payment.setUserId(dto.getUserId());
        payment.setAmountPaid(dto.getAmountPaid());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setMode(dto.getMode());
        payment.setTransactionRef(dto.getTransactionRef());
        BookingRequestEntity bookingRequest = bookingRequestRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking request not found with id: " + dto.getBookingId()));
        payment.setBookingRequest(bookingRequest);
        RentalOptionEntity rentalOptionEntity = rentalOptionRepository.findByPgIdAndSharingTypeAndDurationType(
            bookingRequest.getPg().getId(),
            bookingRequest.getSharingType(),
            RentalType.valueOf(bookingRequest.getRentalType())
        );
        if(rentalOptionEntity == null){
            throw new RuntimeException("Rental option not found for the given booking request");
        }
        if (dto.getAmountPaid() != null && rentalOptionEntity.getCautionDeposit() != null) {
            if (dto.getAmountPaid().compareTo(rentalOptionEntity.getCautionDeposit()) >= 0) {
                payment.setStatus(PaymentStatus.PAID.name());
            } else if (dto.getAmountPaid().compareTo(java.math.BigDecimal.ZERO) > 0) {
                payment.setStatus(PaymentStatus.PARTIALLY_PAID.name());
            } else {
                payment.setStatus(PaymentStatus.PENDING.name());
            }
        } else {
            payment.setStatus(PaymentStatus.PENDING.name());
        }
         cautionDepositPaymentRepository.save(payment);
    }

}
