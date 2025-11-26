package com.easy.stazy.bookings.strategy;

import com.easy.stazy.bookings.dto.response.BookingSummaryDto;
import java.util.List;

public interface BookingFetchStrategy {
    List<BookingSummaryDto> fetch(Long pgId, String status);
}

