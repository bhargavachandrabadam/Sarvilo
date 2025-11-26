package com.easy.stazy.bookings.dto.response;

import com.easy.stazy.bookings.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingSummaryDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -9022931106219245039L;
    private Long pgId;
    private String tenantName;
    private String tenantContactNumber;
    private String rentalType;
    private LocalDate dateOfJoining;
    private Long bookingId;
    private String pgName;
    private BookingStatus status;
    private String message;

    public BookingSummaryDto(Long pgId, String tenantName, String tenantContactNumber, String rentalType, LocalDate dateOfJoining, Long bookingId, String pgName, BookingStatus status) {
        this.pgId = pgId;
        this.tenantName = tenantName;
        this.tenantContactNumber = tenantContactNumber;
        this.rentalType = rentalType;
        this.dateOfJoining = dateOfJoining;
        this.bookingId = bookingId;
        this.pgName = pgName;
        this.status = status;
        this.message = null;
    }
}
