package com.easy.stazy.bookings.controller;

import com.easy.stazy.bookings.dto.response.BookingSummaryDto;
import com.easy.stazy.bookings.service.BookingRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Owner Booking Requests", description = "Endpoints for owner booking requests")
@RestController
@RequestMapping("/v1/owner/bookings")
@RequiredArgsConstructor
public class OwnerBookingRequestController {
    private final BookingRequestService bookingRequestService;

    @Operation(summary = "Get PG Bookings by Status", description = "Fetches booking requests for a specific PG filtered by booking status.")
    @GetMapping("/{pgId}/status")
    public ResponseEntity<List<BookingSummaryDto>> getPgBookings(@PathVariable Long pgId, @RequestParam(required = false) String status) {
        List<BookingSummaryDto> bookings = bookingRequestService.getPgBookings(pgId, status);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Approve Booking Request", description = "Allows owners to approve a booking request.")
    @GetMapping("/approve/{bookingId}")
    public ResponseEntity<?> approveBooking(@PathVariable Long bookingId) {
        bookingRequestService.approveBooking(bookingId);
        return ResponseEntity.ok("Booking approved successfully");
    }

    @GetMapping("/reject/{bookingId}")
    public ResponseEntity<?> rejectBooking(@PathVariable long bookingId){
        bookingRequestService.rejectBooking(bookingId);
        return ResponseEntity.ok("Booking rejected successfully");
    }
}
