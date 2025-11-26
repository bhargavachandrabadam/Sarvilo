package com.easy.stazy.bookings.strategy;

import com.easy.stazy.bookings.dto.response.BookingSummaryDto;
import com.easy.stazy.bookings.repository.BookingRequestRepository;
import java.util.List;

public class AllBookingsFetchStrategy implements BookingFetchStrategy {
    private final BookingRequestRepository bookingRequestRepository;

    public AllBookingsFetchStrategy(BookingRequestRepository bookingRequestRepository) {
        this.bookingRequestRepository = bookingRequestRepository;
    }

    @Override
    public List<BookingSummaryDto> fetch(Long pgId, String status) {
        return bookingRequestRepository.findSummaryByPgId(pgId);
    }
}

