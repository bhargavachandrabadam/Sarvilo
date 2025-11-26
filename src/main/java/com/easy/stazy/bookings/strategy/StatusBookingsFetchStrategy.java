package com.easy.stazy.bookings.strategy;

import com.easy.stazy.bookings.dto.response.BookingSummaryDto;
import com.easy.stazy.bookings.enums.BookingStatus;
import com.easy.stazy.bookings.repository.BookingRequestRepository;
import java.util.ArrayList;
import java.util.List;

public class StatusBookingsFetchStrategy implements BookingFetchStrategy {
    private final BookingRequestRepository bookingRequestRepository;

    public StatusBookingsFetchStrategy(BookingRequestRepository bookingRequestRepository) {
        this.bookingRequestRepository = bookingRequestRepository;
    }

    @Override
    public List<BookingSummaryDto> fetch(Long pgId, String status) {
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            List<BookingSummaryDto> entities = bookingRequestRepository.findByPg_IdAndStatus(pgId, bookingStatus);
            return entities != null ? entities : new ArrayList<>();
        } catch (IllegalArgumentException ex) {
            return new ArrayList<>();
        }
    }
}

