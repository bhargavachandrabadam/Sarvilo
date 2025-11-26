package com.easy.stazy.bookings.validations;

import com.easy.stazy.bookings.entity.BookingRequestEntity;
import org.springframework.stereotype.Service;

@Service
public class BookingRequestValidationServiceImpl implements BookingRequestValidationService {
    @Override
    public void validateOwnerAuthorization(BookingRequestEntity booking, String currentUserId, boolean isOwner) {
        if (currentUserId == null || !isOwner || !booking.getPg().getOwner().getId().equals(Long.valueOf(currentUserId))) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to approve this booking.");
        }
    }

    @Override
    public void validateUserAuthorization(String currentUserId, String requiredRole, Long entityOwnerId) {
        if (currentUserId == null) {
            throw new org.springframework.security.access.AccessDeniedException("User is not authenticated.");
        }
        // You may want to inject a service to check roles, but for now, assume the caller checks the role and passes it here
        if (requiredRole == null || entityOwnerId == null || !entityOwnerId.equals(Long.valueOf(currentUserId))) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to perform this action.");
        }
    }
}
