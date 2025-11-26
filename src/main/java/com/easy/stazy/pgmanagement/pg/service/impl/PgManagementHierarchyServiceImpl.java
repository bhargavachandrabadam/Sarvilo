package com.easy.stazy.pgmanagement.pg.service.impl;

import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.bookings.entity.BookingRequestEntity;
import com.easy.stazy.bookings.repository.BookingRequestRepository;
import com.easy.stazy.pgmanagement.pg.entities.*;
import com.easy.stazy.pgmanagement.pg.dto.PgHierarchyResponse;
import com.easy.stazy.pgmanagement.pg.dto.response.PgManagementHierarchyResponse;
import com.easy.stazy.pgmanagement.pg.repository.BedOccupancyRepository;
import com.easy.stazy.pgmanagement.pg.repository.BedRepository;
import com.easy.stazy.pgmanagement.pg.repository.FloorRepository;
import com.easy.stazy.pgmanagement.pg.repository.RoomRepository;
import com.easy.stazy.pgmanagement.tenant.dto.response.UnassignedTenantDto;
import com.easy.stazy.payments.enums.PaymentStatus;
import com.easy.stazy.pgmanagement.pg.enums.HierarchyFilterStatus;
import com.easy.stazy.pgmanagement.pg.service.PgManagementHierarchyService;
import com.easy.stazy.payments.entity.TenantPaymentEntity;
import com.easy.stazy.payments.repository.TenantPaymentRepository;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PgManagementHierarchyServiceImpl implements PgManagementHierarchyService {
    private static final Logger log = LoggerFactory.getLogger(PgManagementHierarchyServiceImpl.class);

    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final BedOccupancyRepository bedOccupancyRepository;
    private final TenantInfoRepository tenantInfoRepository;
    private final TenantPaymentRepository tenantPaymentRepository; // Already injected
    private final BookingRequestRepository bookingRequestRepository;
    private final PgManagementRepository pgRepository;

    //
    private static PgHierarchyResponse.BedOccupancyHierarchy getBedOccupancyHierarchy(BedOccupancyEntity activeOccupancy) {
        PgHierarchyResponse.BedOccupancyHierarchy occupancyDto = new PgHierarchyResponse.BedOccupancyHierarchy();
        occupancyDto.setBedOccupancyId(activeOccupancy.getId());
        occupancyDto.setStatus(activeOccupancy.getStatus());
        occupancyDto.setJoiningDate(activeOccupancy.getJoiningDate());
        occupancyDto.setExitDate(activeOccupancy.getExitDate());
        occupancyDto.setNotes(activeOccupancy.getNotes());
        occupancyDto.setTenantId(activeOccupancy.getTenant().getId());
        occupancyDto.setTenantName(activeOccupancy.getTenant().getUser().getName());
        occupancyDto.setTenantPhone(activeOccupancy.getTenant().getUser().getPhoneNumber());
        occupancyDto.setTenantEmail(activeOccupancy.getTenant().getUser().getEmailId());
        occupancyDto.setRentalType(activeOccupancy.getRentalType());
        occupancyDto.setSharingType(activeOccupancy.getSharingType());
        return occupancyDto;
    }

    @Override
    public PgManagementHierarchyResponse getPgHierarchy(Long pgId, String floor, LocalDate date, HierarchyFilterStatus status) {
        log.info("getPgHierarchy called with pgId={}, floor={}, date={}, status={}", pgId, floor, date, status);
        validateInputs(pgId, date);
        LocalDate selectedDate = date;
        List<FloorEntity> floors = fetchFloors(pgId, floor);

        // Pass selectedDate to buildPgHierarchy to fetch payments and occupancies
        PgHierarchyResponse pgHierarchy = buildPgHierarchy(pgId, floors, selectedDate);

        List<UnassignedTenantDto> unassignedTenantDtos = fetchUnassignedTenants(pgId);

        // Apply filter AFTER hierarchy is fully built
        applyStatusFilter(pgHierarchy, status);

        return buildResponse(pgHierarchy, unassignedTenantDtos, selectedDate);
    }

    private void validateInputs(Long pgId, LocalDate yearMonth) {
        // 1. Validate inputs
        if (pgId == null || yearMonth == null) {
            log.error("pgId and yearMonth are required. Received pgId={}, yearMonth={}", pgId, yearMonth);
            throw new IllegalArgumentException("pgId and yearMonth are required");
        }
    }

    private List<FloorEntity> fetchFloors(Long pgId, String floor) {
        // 2. Fetch PG → Floor → Room → Bed structure (applying floor filter if present)
        if (floor != null && !floor.isEmpty()) {
            log.debug("Fetching floors for pgId={} and floor={}", pgId, floor);
            return Collections.singletonList(floorRepository.findByPgIdAndFloorNumber(pgId, floor));
        } else {
            log.debug("Fetching all floors for pgId={}", pgId);
            return floorRepository.findByPgId(pgId);
        }
    }

    private PgHierarchyResponse buildPgHierarchy(Long pgId, List<FloorEntity> floors, LocalDate selectedDate) {
        // Efficient batch fetching and in-memory hierarchy construction
        PgHierarchyResponse pgHierarchy = new PgHierarchyResponse();
        pgHierarchy.setPgId(pgId);
        PgManagementEntity pgEntity = pgRepository.findById(pgId).orElse(null);
        if (pgEntity == null) {
            log.error("PG not found for pgId={}", pgId);
            return pgHierarchy;
        }
        pgHierarchy.setPgName(pgEntity.getPgName());
        pgHierarchy.setNumberOfFloors(floors.size());

        // 1. Fetch all rooms for all floors
        List<Long> floorIds = floors.stream().map(FloorEntity::getId).collect(Collectors.toList());
        List<RoomEntity> allRooms = roomRepository.findByFloorIdIn(floorIds);
        var roomsByFloor = allRooms.stream().collect(Collectors.groupingBy(r -> r.getFloor().getId()));

        // 2. Fetch all beds for all rooms (include deleted for history)
        List<Long> roomIds = allRooms.stream().map(RoomEntity::getId).collect(Collectors.toList());
        List<BedEntity> allBeds = bedRepository.findByRoomIdIn(roomIds);
        var bedsByRoom = allBeds.stream().collect(Collectors.groupingBy(b -> b.getRoom().getId()));

        // 3. Fetch all occupancies for all beds that are relevant to the selectedDate
        List<Long> bedIds = allBeds.stream().map(BedEntity::getId).collect(Collectors.toList());
        List<BedOccupancyEntity> occupancies = bedOccupancyRepository.findByBedIdIn(bedIds);
        // Map: bedId -> List<BedOccupancyEntity> (all occupancies for that bed)
        var occupanciesByBed = occupancies.stream().collect(Collectors.groupingBy(o -> o.getBed().getId()));

        // 4. Fetch payment status for all tenants for the selectedDate's month and year
        Set<Long> allTenantIds = occupancies.stream().map(o -> o.getTenant().getId()).collect(Collectors.toSet());
        Set<Long> paidTenantIds = Collections.emptySet();
        if (!allTenantIds.isEmpty()) {
            List<TenantPaymentEntity> payments = tenantPaymentRepository.findByTenantIdInAndMonthAndYear(
                    new ArrayList<>(allTenantIds), selectedDate.getMonthValue(), selectedDate.getYear());
            paidTenantIds = payments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.PAID)
                    .map(p -> p.getTenant().getId())
                    .collect(Collectors.toSet());
        }

        // 5. Build hierarchy in-memory (and set status flags correctly)
        List<PgHierarchyResponse.FloorHierarchy> floorHierarchyList = new ArrayList<>();
        int occupiedBedsInPg = 0;
        int availableBedsInPg = 0;

        for (FloorEntity floor : floors) {
            PgHierarchyResponse.FloorHierarchy floorDto = new PgHierarchyResponse.FloorHierarchy();
            floorDto.setFloorId(floor.getId());
            floorDto.setFloorNumber(floor.getFloorNumber());
            List<RoomEntity> rooms = roomsByFloor.getOrDefault(floor.getId(), List.of());
            floorDto.setNumberOfRooms(rooms.size());

            List<PgHierarchyResponse.RoomHierarchy> roomHierarchyList = new ArrayList<>();
            int occupiedBedsInFloor = 0;
            int availableBedsInFloor = 0;

            for (RoomEntity room : rooms) {
                PgHierarchyResponse.RoomHierarchy roomDto = new PgHierarchyResponse.RoomHierarchy();
                roomDto.setRoomId(room.getId());
                roomDto.setRoomNumber(room.getRoomNumber());
                roomDto.setDescription(room.getDescription());
                List<BedEntity> beds = bedsByRoom.getOrDefault(room.getId(), List.of());
                roomDto.setNumberOfBeds(beds.size());

                List<PgHierarchyResponse.BedHierarchy> bedHierarchyList = new ArrayList<>();
                int occupiedBedsInRoom = 0;
                int availableBedsInRoom = 0;

                for (BedEntity bed : beds) {
                    // If bed is created after selectedDate, skip
                    if (bed.getCreatedAt() != null && bed.getCreatedAt().toLocalDate().isAfter(selectedDate)) {
                        continue;
                    }

                    // Find the occupancy for this bed relevant to the selectedDate
                    List<BedOccupancyEntity> bedOccupancies = occupanciesByBed.getOrDefault(bed.getId(), List.of());
                    BedOccupancyEntity relevantOccupancy = bedOccupancies.stream()
                            .filter(o -> !selectedDate.isBefore(o.getJoiningDate()) && (o.getExitDate() == null || !selectedDate.isAfter(o.getExitDate())))
                            .findFirst().orElse(null);

                    // If bed is deleted and has no occupancy for the selected date, skip
                    if (bed.getIsDeleted() && relevantOccupancy == null) {
                        continue;
                    }

                    // If bed is not deleted and has no occupancy, show as vacant (handled below)
                    // If bed is deleted but had occupancy on selectedDate, show it (handled below)

                    PgHierarchyResponse.BedHierarchy bedDto = new PgHierarchyResponse.BedHierarchy();
                    bedDto.setBedId(bed.getId());
                    bedDto.setBedNumber(bed.getBedNumber());
                    bedDto.setDescription(bed.getDescription());
                    bedDto.setNotes(bed.getNotes());
                    // Optionally, add isDeleted flag to DTO if needed
                    // bedDto.setIsDeleted(bed.getIsDeleted());

                    if (relevantOccupancy != null) {
                        PgHierarchyResponse.BedOccupancyHierarchy occupancyDto = getBedOccupancyHierarchy(relevantOccupancy);
                        bedDto.setActiveOccupancy(occupancyDto);
                        bedDto.setOccupied(true);
                        bedDto.setAssigned(true);
                        bedDto.setInNoticePeriod(relevantOccupancy.getExitDate() != null && !selectedDate.isAfter(relevantOccupancy.getExitDate()) && !selectedDate.isBefore(relevantOccupancy.getJoiningDate()));
                        if (paidTenantIds.contains(relevantOccupancy.getTenant().getId())) {
                            bedDto.setRentStatus(PgHierarchyResponse.RentStatus.PAID);
                        } else {
                            bedDto.setRentStatus(PgHierarchyResponse.RentStatus.UNPAID);
                        }
                        bedDto.setUnderMaintenance(false);
                        bedDto.setReadyToOccupy(false);
                        occupiedBedsInRoom++;
                        occupiedBedsInFloor++;
                        occupiedBedsInPg++;
                    } else {
                        bedDto.setActiveOccupancy(null);
                        bedDto.setOccupied(false);
                        bedDto.setAssigned(false);
                        bedDto.setInNoticePeriod(false);
                        bedDto.setRentStatus(PgHierarchyResponse.RentStatus.NOT_APPLICABLE);
                        String notes = bed.getNotes();
                        boolean isUnderMaintenance = (notes != null && (notes.toLowerCase().contains("under maintenance") || notes.equalsIgnoreCase("UNDER_MAINTENANCE")));
                        bedDto.setUnderMaintenance(isUnderMaintenance);
                        bedDto.setReadyToOccupy(!isUnderMaintenance);
                        if (!isUnderMaintenance) {
                            availableBedsInRoom++;
                            availableBedsInFloor++;
                            availableBedsInPg++;
                        }
                    }
                    bedHierarchyList.add(bedDto);
                }

                roomDto.setBeds(bedHierarchyList);
                roomDto.setNumberOfOccupiedBedsInRoomLevel(occupiedBedsInRoom);
                roomDto.setNumberOfAvailableBedsInRoomLevel(availableBedsInRoom);
                roomHierarchyList.add(roomDto);
            }

            floorDto.setRooms(roomHierarchyList);
            floorDto.setNumberOfOccupiedBedsInFloorLevel(occupiedBedsInFloor);
            floorDto.setNumberOfAvailableBedsInFloorLevel(availableBedsInFloor);
            floorHierarchyList.add(floorDto);
        }

        pgHierarchy.setFloors(floorHierarchyList);
        pgHierarchy.setNumberOfOccupiedBedsInPgLevel(occupiedBedsInPg);
        pgHierarchy.setNumberOfAvailableBedsInPgLevel(availableBedsInPg);
        return pgHierarchy;
    }

    private List<UnassignedTenantDto> fetchUnassignedTenants(Long pgId) {
        // Fetch all tenants for this PG
        List<TenantInfoEntity> tenantInfos = tenantInfoRepository.findByPgId(pgId);
        if (tenantInfos.isEmpty()) return List.of();
        // Get all tenant IDs
        List<Long> tenantIds = tenantInfos.stream().map(TenantInfoEntity::getId).toList();
        // Fetch all active bed occupancies for these tenants (assigned beds)
        // This finds any active occupancy, not just for the selected month.
        List<BedOccupancyEntity> activeOccupancies = bedOccupancyRepository.findActiveByTenantIdIn(tenantIds);
        // Collect assigned tenant IDs
        var assignedTenantIds = activeOccupancies.stream().map(o -> o.getTenant().getId()).collect(java.util.stream.Collectors.toSet());
        // Only include tenants who are not assigned to any bed
        return tenantInfos.stream()
                .filter(tenant -> !assignedTenantIds.contains(tenant.getId()))
                .map(tenant -> {
                    UnassignedTenantDto dto = new UnassignedTenantDto();
                    dto.setTenantId(tenant.getId());
                    dto.setTenantName(tenant.getUser().getName());
                    dto.setTenantPhone(tenant.getUser().getPhoneNumber());
                    dto.setTenantEmail(tenant.getUser().getEmailId());
                    // Assuming TenantInfoEntity has createdAt
                    dto.setApprovedAt(LocalDate.from(tenant.getCreatedAt()));
                    // Fetch sharingType from the latest booking request for this user and PG
                    BookingRequestEntity booking = bookingRequestRepository.findTopByUserIdAndPgIdOrderByCreatedAtDesc(tenant.getUser().getId(), pgId);
                    if (booking != null) {
                        dto.setSharingType(booking.getSharingType());
                        dto.setRentalType(booking.getRentalType());
                    }
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    private void applyStatusFilter(PgHierarchyResponse pgHierarchy, HierarchyFilterStatus status) {
        // Status filter logic
        if (status != null && status != HierarchyFilterStatus.ALL) {
            log.debug("Applying status filter: {}", status);
            for (PgHierarchyResponse.FloorHierarchy floorDto : pgHierarchy.getFloors()) {
                for (PgHierarchyResponse.RoomHierarchy roomDto : floorDto.getRooms()) {
                    // Use matchesStatus for ALL filter types
                    List<PgHierarchyResponse.BedHierarchy> filteredBeds = roomDto.getBeds().stream()
                            .filter(bed -> matchesStatus(bed, status))
                            .collect(Collectors.toList());

                    roomDto.setBeds(filteredBeds);
                }
                // Optional: Hide rooms with no matching beds
                // roomDto.setRooms(roomDto.getRooms().stream().filter(r -> !r.getBeds().isEmpty()).collect(Collectors.toList()));
            }
            // Optional: Hide floors with no matching beds
            // pgHierarchy.setFloors(pgHierarchy.getFloors().stream().filter(f -> !f.getRooms().isEmpty()).collect(Collectors.toList()));
        }
    }

    private PgManagementHierarchyResponse buildResponse(PgHierarchyResponse pgHierarchy, List<UnassignedTenantDto> unassignedTenantDtos, LocalDate selectedDate) {
        // Build and return PgManagementHierarchyResponse
        PgManagementHierarchyResponse response = new PgManagementHierarchyResponse();
        response.setPgHierarchy(pgHierarchy);
        response.setUnassignedTenantDtos(unassignedTenantDtos);
        response.setSelectedMonth(selectedDate); // Now this is the actual date
        return response;
    }


    // This method was already correct and requires no changes.
    // It works now because buildPgHierarchy correctly sets the flags it checks.
    private boolean matchesStatus(PgHierarchyResponse.BedHierarchy bed, HierarchyFilterStatus status) {
        if (status == null || status == HierarchyFilterStatus.ALL) {
            return true;
        }

        PgHierarchyResponse.RentStatus rentStatus = bed.getRentStatus();

        return switch (status) {
            case PAID -> rentStatus == PgHierarchyResponse.RentStatus.PAID;
            case UNPAID -> rentStatus == PgHierarchyResponse.RentStatus.UNPAID;
            case VACANT -> !bed.isOccupied() && bed.isReadyToOccupy();
            case UNDER_MAINTENANCE -> !bed.isOccupied() && bed.isUnderMaintenance();
            case NOTICE_PERIOD -> bed.isInNoticePeriod();
            case UNASSIGNED -> !bed.isAssigned(); // This will show both VACANT and UNDER_MAINTENANCE
            default -> true; // Default to ALL if enum is not matched
        };
    }
}
