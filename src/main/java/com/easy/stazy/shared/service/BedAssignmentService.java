package com.easy.stazy.shared.service;

import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.pg.entities.*;
import com.easy.stazy.pgmanagement.pg.enums.BedOccupancyStatus;
import com.easy.stazy.pgmanagement.pg.repository.*;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.shared.common.constants.constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for handling bed assignment and creation logic in the PG management system.
 * <p>
 * This service provides a single point of logic for resolving or creating Floor, Room, and Bed entities
 * based on the provided PG, floor, room, and bed details. It also ensures that rental options are set
 * for new rooms based on sharing type and rental type, and prevents double occupancy by checking for
 * existing active bed occupancies.
 * <p>
 * By centralizing this logic, the application avoids code duplication and ensures consistent behavior
 * across different modules (such as tenant creation and Excel import).
 */

@Service
@RequiredArgsConstructor
public class BedAssignmentService {

    private final FloorRepository floorRepository;

    private final RoomRepository roomRepository;

    private final BedRepository bedRepository;

    private final BedOccupancyRepository bedOccupancyRepository;

    private final PgManagementRepository pgManagementRepository;

    private final RentalOptionRepository rentalOptionRepository;

    public BedEntity getOrCreateBedEntity(Long pgId, String floorNumber, String roomNumber, String bedNumber, String rentalType, String sharingType) {
        // Check for floor
        FloorEntity floor = floorRepository.findByPgIdAndFloorNumber(pgId, floorNumber);
        if (floor == null) {
            floor = new FloorEntity();
            floor.setPg(pgManagementRepository.getReferenceById(pgId));
            floor.setFloorNumber(floorNumber);
            floor = floorRepository.save(floor);
        }
        // Check for room
        RoomEntity room = roomRepository.findByFloorIdAndRoomNumber(floor.getId(), roomNumber);
        if (room == null) {
            room = new RoomEntity();
            room.setFloor(floor);
            room.setRoomNumber(roomNumber);
            // Set rental option based on sharing type if provided
            if (sharingType != null) {
                List<RentalOptionEntity> rentalOption = rentalOptionRepository.findByPgIdAndSharingTypeNative(pgId, sharingType);
                if (rentalOption == null || rentalOption.isEmpty()) {
                    throw new RuntimeException("No rental option found for PG: " + pgId + ", sharing type: " + sharingType);
                }
                room.setRentalOptions(rentalOption);
            }
            room = roomRepository.save(room);
        }
        // Check for bed
        BedEntity bed = bedRepository.findByRoomIdAndBedNumberAndIsDeleted(room.getId(), bedNumber, false);
        if (bed == null) {
            bed = new BedEntity();
            bed.setRoom(room);
            bed.setBedNumber(bedNumber);
            bed = bedRepository.save(bed);
        }
        // Check if any tenant is ACTIVE for this bed
        if (bedOccupancyRepository.existsByBed_IdAndStatus(bed.getId(), constants.ACTIVE)) {
            throw new IllegalStateException("Already another tenant is active for this bed number.");
        }

        return bed;
    }

    /**
     * Creates an ACTIVE bed occupancy record for the given tenant and bed.
     * Sets the joining date to today and persists the record.
     *
     * @param bed    the target bed to occupy
     * @param tenant the tenant occupying the bed
     */
    public void createBedOccupancy(BedEntity bed, TenantInfoEntity tenant, String rentalType, String sharingType) {
        boolean isOccupied = bedOccupancyRepository.findTopByBedAndStatusOrderByJoiningDateDesc(bed, BedOccupancyStatus.ACTIVE.toString())
                .isPresent();
        if (isOccupied) {
            throw new IllegalStateException("Selected bed is already occupied and active");
        }
        // Create new ACTIVE bed occupancy
        BedOccupancyEntity bo = new BedOccupancyEntity();
        bo.setBed(bed);
        bo.setTenant(tenant);
        bo.setStatus(BedOccupancyStatus.ACTIVE.toString());
        bo.setJoiningDate(LocalDate.now());
        bo.setRentalType(rentalType);
        bo.setSharingType(sharingType);
        bedOccupancyRepository.save(bo);
    }

    public BedOccupancyEntity createAndReturnBedOccupancy(BedEntity bed, TenantInfoEntity tenant, String rentalType, String sharingType) {
        boolean isOccupied = bedOccupancyRepository.findTopByBedAndStatusOrderByJoiningDateDesc(bed, BedOccupancyStatus.ACTIVE.toString())
                .isPresent();
        if (isOccupied) {
            throw new IllegalStateException("Selected bed is already occupied and active");
        }
        // Create new ACTIVE bed occupancy
        BedOccupancyEntity bo = new BedOccupancyEntity();
        bo.setBed(bed);
        bo.setTenant(tenant);
        bo.setStatus(BedOccupancyStatus.ACTIVE.toString());
        bo.setJoiningDate(LocalDate.now());
        bo.setRentalType(rentalType);
        bo.setSharingType(sharingType);
        bedOccupancyRepository.save(bo);
        return bo;
    }

    public void vacateBedOccupancy(BedOccupancyEntity bedOccupancy) {
        if (bedOccupancy != null && BedOccupancyStatus.ACTIVE.name().equalsIgnoreCase(bedOccupancy.getStatus())) {
            bedOccupancy.setStatus(BedOccupancyStatus.VACATED.name());
            bedOccupancy.setUpdatedAt(java.time.LocalDateTime.now());
            bedOccupancyRepository.save(bedOccupancy);
        }
    }

    @Transactional
    public void saveAllBedOccupancies(List<BedOccupancyEntity> occupanciesToSave) {
        bedOccupancyRepository.saveAll(occupanciesToSave);
    }
}
