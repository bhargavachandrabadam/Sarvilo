package com.easy.stazy.bookings.controller;

import com.easy.stazy.bookings.dto.response.BookingListResponseDto;
import com.easy.stazy.bookings.dto.request.BookingRequestDto;
import com.easy.stazy.payments.dto.request.CautionDepositPaymentRequestDto;
import com.easy.stazy.bookings.enums.BookingStatus;
import com.easy.stazy.bookings.service.BookingRequestService;
import com.easy.stazy.payments.service.CautionDepositPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(name = "User Booking Requests", description = "Endpoints for user booking requests")
@RestController
@RequestMapping("/v1/users/bookings")
@RequiredArgsConstructor
public class UserBookingRequestController {
    private final BookingRequestService bookingRequestService;
    private final CautionDepositPaymentService cautionDepositPaymentService;

    @Operation(summary = "Create Booking Request", description = "Allows users to create a new booking request with optional profile photo upload.")
    @PostMapping(value = "/booking-request",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBooking(
            @ModelAttribute BookingRequestDto dto,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto) {
        bookingRequestService.createBookingRequest(dto,profilePhoto);
        return ResponseEntity.status(201).body("Booking request created successfully");
    }

    @Operation(summary = "Get Bookings by User ID and Status", description = "Fetches booking requests for a specific user filtered by booking status.")
    @GetMapping("/user-booking-list")
    public ResponseEntity<?> getBookingsByUserIdAndStatus(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "PENDING") BookingStatus status) {
        List<BookingListResponseDto> bookings = bookingRequestService.  getBookingsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Submit Caution Deposit Payment", description = "Allows users to submit a caution deposit payment for a booking.")
    @PostMapping("/caution-deposit-payment")
    public ResponseEntity<?> submitCautionDepositPayment(@RequestBody CautionDepositPaymentRequestDto dto) {
         cautionDepositPaymentService.saveCautionDepositPayment(dto);
        // TODO: Notify owner about new caution deposit payment
        return ResponseEntity.status(201).body("Caution deposit payment submitted successfully");
    }
}
