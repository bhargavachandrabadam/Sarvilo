package com.easy.stazy.pgmanagement.pg.service.impl;


import com.easy.stazy.pgmanagement.pg.dto.response.PgHierarchyResponse;
import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.pg.entities.RoomEntity;
import com.easy.stazy.pgmanagement.pg.service.PgHeirarchyService;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Implementation of the PgService interface for managing PG (Paying Guest) hierarchy and related operations.
 * <p>
 * This service provides business logic for retrieving the hierarchical structure of a PG, including its floors,
 * rooms, and beds, along with occupancy details. It interacts with the EasyStazyAppRepository to fetch PG data
 * and constructs a detailed hierarchy response suitable for API output or UI display. The service includes methods
 * for mapping entities to DTOs at each level of the hierarchy and for calculating statistics such as the number of
 * occupied and available beds at the PG, floor, and room levels. All read operations are transactional and optimized
 * for performance and consistency.
 * <p>
 * Typical use cases include:
 * <ul>
 *   <li>Retrieving the full hierarchy of a PG for management dashboards</li>
 *   <li>Calculating occupancy statistics at various levels (PG, floor, room)</li>
 *   <li>Mapping entity data to DTOs for API responses</li>
 * </ul>
 *
 */
@RequiredArgsConstructor
@Service
public class PgHeirarchyServiceImpl implements PgHeirarchyService {
    private final PgManagementRepository pgManagementRepository;

    /**
     * Maps a RoomEntity to a RoomHierarchy DTO.
     * <p>
     * This private static method populates the RoomHierarchy DTO with room details.
     *
     * @param room the RoomEntity to map
     * @return the populated RoomHierarchy DTO
     */
    private static PgHierarchyResponse.RoomHierarchy getRoomHierarchy(RoomEntity room) {
        PgHierarchyResponse.RoomHierarchy roomDto = new PgHierarchyResponse.RoomHierarchy();
        roomDto.setRoomId(room.getId());
        roomDto.setRoomNumber(room.getRoomNumber());
        roomDto.setType(room.getType());
        roomDto.setDescription(room.getDescription());
        return roomDto;
    }

    /**
     * Retrieves the full hierarchy of a PG (Paying Guest) by its ID, including floors, rooms, beds, and occupancy details.
     * <p>
     * This method fetches the PG entity from the repository, constructs a PgHierarchyResponse DTO, and populates it with
     * detailed information about each floor, room, and bed. It also calculates and sets statistics such as the number of
     * occupied and available beds at the PG level. The method is transactional and read-only for performance and consistency.
     *
     * @param pgId the unique identifier of the PG to retrieve
     * @return a PgHierarchyResponse object containing the full hierarchy and statistics for the specified PG
     * @throws ResourceNotFoundException if the PG with the given ID does not exist
     */
    @Transactional(readOnly = true)
    @Override
    public PgHierarchyResponse getPgHierarchy(Long pgId) {
        PgManagementEntity pg = pgManagementRepository.findById(pgId)
                .orElseThrow(() -> new ResourceNotFoundException("PG not found"));
        PgHierarchyResponse response = new PgHierarchyResponse();
        response.setPgId(pg.getId());
        response.setPgName(pg.getPgName());
        if (pg.getFloors() != null) {
            var floorList = pg.getFloors().stream().map(floor -> {
                PgHierarchyResponse.FloorHierarchy floorDto = new PgHierarchyResponse.FloorHierarchy();
                return getFloorHierarchy(floor, floorDto);
            }).toList();
            response.setFloors(floorList);
            response.setNumberOfFloors(floorList.size());
            int occupied = floorList.stream().mapToInt(PgHierarchyResponse.FloorHierarchy::getNumberOfOccupiedBedsInFloorLevel).sum();
            int totalBeds = floorList.stream().flatMap(f -> f.getRooms() == null ? java.util.stream.Stream.empty() : f.getRooms().stream())
                    .mapToInt(PgHierarchyResponse.RoomHierarchy::getNumberOfBeds).sum();
            response.setNumberOfOccupiedBedsInPgLevel(occupied);
            response.setNumberOfAvailableBedsInPgLevel(Math.max(0, totalBeds - occupied));
        }
        return response;
    }

    /**
     * Maps a FloorEntity and its associated rooms and beds to a FloorHierarchy DTO.
     * <p>
     * This method populates the FloorHierarchy DTO with floor details and recursively maps all rooms and their beds.
     * It also calculates and sets statistics such as the number of rooms, occupied beds, and available beds at the floor level.
     * The method is transactional and read-only for performance and consistency.
     *
     * @param floor    the FloorEntity to map
     * @param floorDto the FloorHierarchy DTO to populate
     * @return the populated FloorHierarchy DTO
     */
    @Override
    public PgHierarchyResponse.FloorHierarchy getFloorHierarchy(FloorEntity floor, PgHierarchyResponse.FloorHierarchy floorDto) {
        floorDto.setFloorId(floor.getId());
        floorDto.setFloorNumber(floor.getFloorNumber());
        if (floor.getRooms() != null) {
            var roomList = floor.getRooms().stream().map(room -> {
                PgHierarchyResponse.RoomHierarchy roomDto = getRoomHierarchy(room);
                if (room.getBeds() != null) {
                    var bedList = room.getBeds().stream().map(this::getBedHierarchy).toList();
                    roomDto.setBeds(bedList);
                    roomDto.setNumberOfBeds(bedList.size());
                    int occupiedBeds = (int) bedList.stream().filter(PgHierarchyResponse.BedHierarchy::isOccupied).count();
                    roomDto.setNumberOfOccupiedBedsInRoomLevel(occupiedBeds);
                    roomDto.setNumberOfAvailableBedsInRoomLevel(Math.max(0, bedList.size() - occupiedBeds));
                }
                return roomDto;
            }).toList();
            floorDto.setRooms(roomList);
            floorDto.setNumberOfRooms(roomList.size());
            int occupied = roomList.stream().mapToInt(PgHierarchyResponse.RoomHierarchy::getNumberOfOccupiedBedsInRoomLevel).sum();
            int totalBeds = roomList.stream().mapToInt(PgHierarchyResponse.RoomHierarchy::getNumberOfBeds).sum();
            floorDto.setNumberOfOccupiedBedsInFloorLevel(occupied);
            floorDto.setNumberOfAvailableBedsInFloorLevel(Math.max(0, totalBeds - occupied));
        }
        return floorDto;
    }

    /**
     * Maps a BedEntity and its occupancy details to a BedHierarchy DTO.
     * <p>
     * This method populates the BedHierarchy DTO with bed details and, if the bed is occupied, includes the active occupancy details.
     * The method is transactional and read-only for performance and consistency.
     *
     * @param bed the BedEntity to map
     * @return the populated BedHierarchy DTO
     */
    @Override
    public PgHierarchyResponse.BedHierarchy getBedHierarchy(BedEntity bed) {
        PgHierarchyResponse.BedHierarchy bedDto = new PgHierarchyResponse.BedHierarchy();
        bedDto.setBedId(bed.getId());
        bedDto.setBedNumber(bed.getBedNumber());
        bedDto.setDescription(bed.getDescription());
        bedDto.setMonthlyRent(bed.getMonthlyRent());
        if (bed.getBedOccupancies() != null) {
            bed.getBedOccupancies().stream()
                    .filter(Objects::nonNull)
                    .filter(bo -> bo.getStatus() != null && "ACTIVE".equalsIgnoreCase(bo.getStatus()))
                    .findFirst()
                    .ifPresent(active -> {
                        PgHierarchyResponse.BedOccupancyHierarchy occDto = getBedOccupancyHierarchy(active);
                        bedDto.setActiveOccupancy(occDto);
                        bedDto.setOccupied(true);
                    });
        }
        return bedDto;
    }

    /**
     * Maps a BedOccupancyEntity to a BedOccupancyHierarchy DTO.
     * <p>
     * This method populates the BedOccupancyHierarchy DTO with occupancy details, including tenant information if available.
     * The method is transactional and read-only for performance and consistency.
     *
     * @param active the BedOccupancyEntity to map
     * @return the populated BedOccupancyHierarchy DTO
     */
    @Override
    public PgHierarchyResponse.BedOccupancyHierarchy getBedOccupancyHierarchy(BedOccupancyEntity active) {
        PgHierarchyResponse.BedOccupancyHierarchy occDto = new PgHierarchyResponse.BedOccupancyHierarchy();
        occDto.setBedOccupancyId(active.getId());
        occDto.setStatus(active.getStatus());
        occDto.setJoiningDate(active.getJoiningDate());
        occDto.setExitDate(active.getExitDate());
        occDto.setNotes(active.getNotes());
        if (active.getTenant() != null) {
            occDto.setTenantId(active.getTenant().getId());
            if (active.getTenant().getUser() != null) {
                occDto.setTenantName(active.getTenant().getUser().getName());
                occDto.setTenantPhone(active.getTenant().getUser().getPhoneNumber());
                occDto.setTenantEmail(active.getTenant().getUser().getEmailId());
            }
        }
        return occDto;
    }
}
