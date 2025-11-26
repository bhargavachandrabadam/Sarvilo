package com.easy.stazy.bookings.dto.response;

import com.easy.stazy.payments.enums.PaymentStatus;
import com.easy.stazy.bookings.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingListResponseDto {

    private Long pgId;
    private String pgName;
    private String pgLocation;
    private Double ratingAvgCount;
    private Integer reviewsCount;
    private String tenantName;
    private String rentalType;
    private LocalDate dateOfJoining;
    private BookingStatus status;
    private CautionDepositDetailsDto cautionDepositDetails;

    @Data
    public static class CautionDepositDetailsDto {
        private Double amountPaid;
        private Date paymentDate;
        private PaymentStatus paymentStatus;
    }
}
