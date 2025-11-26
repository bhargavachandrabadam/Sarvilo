package com.easy.stazy.payments.entity;

import com.easy.stazy.bookings.entity.BookingRequestEntity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "caution_deposit_payments")
public class CautionDepositPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal amountPaid;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private String mode; // CASH or ONLINE

    @Column
    private String transactionRef;

    @Column(nullable = false)
    private String status; // PAID, PENDING, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", referencedColumnName = "id", nullable = false)
    private BookingRequestEntity bookingRequest;
}
