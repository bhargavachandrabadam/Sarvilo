package com.easy.stazy.bookings.validations;

import com.easy.stazy.bookings.entity.BookingRequestEntity;

public interface BookingRequestValidationService {
    void validateOwnerAuthorization(BookingRequestEntity booking, String currentUserId, boolean isOwner);
    void validateUserAuthorization(String currentUserId, String requiredRole, Long entityOwnerId);
}
