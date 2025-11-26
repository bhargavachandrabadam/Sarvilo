package com.easy.stazy.pgmanagement.pg.validations.impl;

import com.easy.stazy.pgmanagement.pg.dto.request.BedRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.pgmanagement.pg.repository.BedRepository;
import com.easy.stazy.pgmanagement.pg.validations.BedValidator;
import com.easy.stazy.shared.common.exception.AlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of BedValidator to ensure no duplicate bed exists in a room.
 */
@Service
@RequiredArgsConstructor
public class BedValidatorImpl implements BedValidator {

    private final BedRepository bedRepository;

    /**
     * Checks if a bed with the same number already exists in the given room.
     * Throws AlreadyExistsException if duplicate found.
     *
     * @param roomId the room id
     * @param dto    the BedRequestDto containing bed details
     * @throws AlreadyExistsException if a duplicate bed exists
     */
    @Override
    public void validateBedUniqueness(Long roomId, BedRequestDto dto) {
        BedEntity existingBed = bedRepository.findByBedNumberAndRoomIdAndIsDeleted(dto.getBedNumber(), roomId, false);
        if (existingBed != null) {
            throw new AlreadyExistsException("Bed with number '" + dto.getBedNumber() + "' already exists in this room.");
        }
    }

    /**
     * Validates that the number of beds created for a room matches the sharing type (e.g., 2 Sharing, 3 Sharing).
     *
     * @param sharingType     The sharing type string (e.g., "2 Sharing", "3 Sharing").
     * @param currentBedCount The number of beds created for the room.
     * @throws IllegalArgumentException if the bed count does not match the sharing type.
     */
    @Override
    public void validateBedCountForSharing(String sharingType, int currentBedCount) {
        if (sharingType == null || !sharingType.matches("\\d+ Sharing")) {
            throw new IllegalArgumentException("Invalid sharing type: " + sharingType);
        }
        int maxBedCount = Integer.parseInt(sharingType.split(" ")[0]);
        if (currentBedCount > maxBedCount) {
            throw new IllegalArgumentException("Number of beds exceeds the allowed sharing type limit.");
        }
    }
}

