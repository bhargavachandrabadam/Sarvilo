package com.easy.stazy.bookings.service;

import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.bookings.dto.response.BookingListResponseDto;
import com.easy.stazy.bookings.dto.request.BookingRequestDto;
import com.easy.stazy.bookings.dto.response.BookingSummaryDto;
import com.easy.stazy.bookings.entity.BookingRequestEntity;
import com.easy.stazy.bookings.enums.BookingStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookingRequestService {
    void createBookingRequest(BookingRequestDto dto, MultipartFile profilePhoto);
    List<BookingRequestEntity> getBookingsByPgId(Long pgId);
    List<BookingRequestEntity> getBookingsByUser(UsersEntity user);
    List<BookingSummaryDto> getBookingsByPgIdAndStatus(long pgId, BookingStatus status);
    void approveBooking(Long bookingId);
    void rejectBooking(Long bookingId);
    List<BookingSummaryDto> getPgBookings(Long pgId, String status);
    List<BookingListResponseDto> getBookingsByUserIdAndStatus(Long userId, BookingStatus status);
}