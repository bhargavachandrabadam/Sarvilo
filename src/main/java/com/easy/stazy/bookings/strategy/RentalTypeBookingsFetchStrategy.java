package com.easy.stazy.bookings.strategy;

import com.easy.stazy.bookings.dto.response.BookingSummaryDto;
import com.easy.stazy.bookings.repository.BookingRequestRepository;
import java.util.ArrayList;
import java.util.List;

public class RentalTypeBookingsFetchStrategy implements BookingFetchStrategy {
    private final BookingRequestRepository bookingRequestRepository;

    public RentalTypeBookingsFetchStrategy(BookingRequestRepository bookingRequestRepository) {
        this.bookingRequestRepository = bookingRequestRepository;
    }

    @Override
    public List<BookingSummaryDto> fetch(Long pgId, String status) {
        List<BookingSummaryDto> entities = bookingRequestRepository.findByPgIdAndRentalType(pgId, status.toUpperCase());
        return entities != null ? entities : new ArrayList<>();
    }
}

