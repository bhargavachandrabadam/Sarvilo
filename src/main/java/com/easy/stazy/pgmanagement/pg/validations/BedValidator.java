package com.easy.stazy.pgmanagement.pg.validations;

import com.easy.stazy.pgmanagement.pg.dto.request.BedRequestDto;

/**
 * Validator interface for Bed entity to ensure no duplicate bed exists in a room.
 */
public interface BedValidator {
    /**
     * Validates that a bed with the given bed number does not already exist in the specified room.
     *
     * @param roomId the room id
     * @param dto    the BedRequestDto containing bed details
     * @throws IllegalArgumentException if a duplicate bed exists
     */
    void validateBedUniqueness(Long roomId, BedRequestDto dto);

    /**
     * Validates that the number of beds created for a room matches the sharing type (e.g., 2 Sharing, 3 Sharing).
     *
     * @param sharingType The sharing type string (e.g., "2 Sharing", "3 Sharing").
     * @param bedCount    The number of beds created for the room.
     * @throws IllegalArgumentException if the bed count does not match the sharing type.
     */
    void validateBedCountForSharing(String sharingType, int bedCount);
}

