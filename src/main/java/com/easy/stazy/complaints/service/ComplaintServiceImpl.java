package com.easy.stazy.complaints.service;

import com.easy.stazy.complaints.repository.ComplaintRepository;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.complaints.dto.request.ComplaintRequestDto;
import com.easy.stazy.complaints.dto.response.ComplaintResponseDto;
import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.complaints.entity.ComplaintEntity;
import com.easy.stazy.pgmanagement.pg.entities.FloorEntity;
import com.easy.stazy.pgmanagement.pg.entities.RoomEntity;
import com.easy.stazy.complaints.enums.ComplaintStatus;
import com.easy.stazy.pgmanagement.pg.repository.BedRepository;
import com.easy.stazy.notifications.service.FcmNotificationService;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the ComplaintService interface for managing complaints in the hostel module.
 * <p>
 * This service provides business logic for creating, retrieving, and updating complaints related to PG (Paying Guest)
 * accommodations. It interacts with repositories for tenants, PGs, complaints, and beds to persist and retrieve data.
 * The service supports operations such as creating a new complaint for a tenant, fetching all complaints for a PG,
 * and updating the status of a complaint. All data-modifying operations are transactional to ensure consistency.
 * <p>
 * Typical use cases include:
 * <ul>
 *   <li>Creating a new complaint for a tenant in a PG</li>
 *   <li>Retrieving all complaints for a specific PG</li>
 *   <li>Updating the status of a complaint (e.g., marking as resolved)</li>
 * </ul>
 *
 */
@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final TenantInfoRepository tenantInfoRepository;

    private final PgManagementRepository pgRepository;

    private final ComplaintRepository complaintRepository;

    private final BedRepository bedRepository;

    private final FcmNotificationService fcmNotificationService;

    private static ComplaintResponseDto getComplaintResponseDto(ComplaintEntity complaintEntity) {
        ComplaintResponseDto dto = new ComplaintResponseDto();
        dto.setComplaintId(complaintEntity.getId());
        dto.setReferenceNumber(complaintEntity.getReferenceNumber());
        dto.setTenantName(complaintEntity.getTenant().getUser().getName());
        dto.setFloorNumber(complaintEntity.getFloor() != null ? complaintEntity.getFloor().getFloorNumber() : null);
        dto.setRoomNumber(complaintEntity.getRoom() != null ? complaintEntity.getRoom().getRoomNumber() : null);
        dto.setBedNumber(complaintEntity.getBed() != null ? complaintEntity.getBed().getBedNumber() : null);
        dto.setDescription(complaintEntity.getDescription());
        dto.setStatus(String.valueOf(complaintEntity.getStatus()));
        dto.setCreatedAt(complaintEntity.getCreatedAt());
        return dto;
    }

    /**
     * This method creates a new complaint for a tenant in a specific PG (Paying Guest) accommodation.
     * It first validates the existence of the PG and tenant using their IDs.
     * If both are valid, it creates a new ComplaintEntity, sets its properties based on the provided DTO,
     * and saves it to the database. The complaint is initialized with a status of PENDING and the current date as the creation date.
     *
     * @param pgId     The ID of the PG accommodation.
     * @param tenantId The ID of the tenant lodging the complaint.
     *                 The ComplaintRequestDto containing details of the complaint.
     */

    @Transactional
    @Override
    public void createComplaint(long pgId, long tenantId, ComplaintRequestDto dto) {
        Result result = validatePgAndTenant(pgId, tenantId);
        ComplaintEntity complaint = new ComplaintEntity();
        complaint.setReferenceNumber(UUID.randomUUID().toString());
        complaint.setTenant(result.tenant());
        complaint.setPg(result.pg());
        BedEntity bed = getBedEntity(pgId, dto);
        RoomEntity room = bed.getRoom();
        FloorEntity floor = room.getFloor();
        complaint.setFloor(floor);
        complaint.setRoom(room);
        complaint.setBed(bed);
        complaint.setDescription(dto.getDescription());
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setCreatedAt(LocalDate.now());
        complaintRepository.save(complaint);
        // Send push notification to owner
        Long ownerId = result.pg().getOwner().getId();
        Map<String, String> payload = Map.of(
                "type", "COMPLAINT_RAISED",
                "pgId", String.valueOf(pgId),
                "tenantId", String.valueOf(tenantId),
                "complaintId", String.valueOf(complaint.getId())
        );
        fcmNotificationService.sendNotificationAndLog(
                ownerId,
                UserType.OWNER,
                "New Complaint Raised",
                "A new complaint has been raised by a tenant in your PG.",
                payload
        );
    }

    private BedEntity getBedEntity(long pgId, ComplaintRequestDto dto) {
        return bedRepository.findBedByDetails(
                dto.getFloor(),
                dto.getRoomNumber(),
                dto.getBedNumber(),
                pgId
        ).orElseThrow(() -> new ResourceNotFoundException("Bed not found"));
    }

    private Result validatePgAndTenant(long pgId, long tenantId) {
        TenantInfoEntity tenant = tenantInfoRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        PgManagementEntity pg = pgRepository.findById(pgId)
                .orElseThrow(() -> new ResourceNotFoundException("PG not found"));
        return new Result(tenant, pg);
    }

    /**
     * Retrieves all complaints for a given PG (Paying Guest) by its ID, with optional status filtering.
     * <p>
     * This method fetches all ComplaintEntity records associated with the specified PG ID using the complaintRepository.
     * If a status filter is provided, it returns only complaints matching that status. If the filter is "All" or null/empty, it returns all complaints.
     * Supported status filters: "All", "Pending action", "Work in progress", "Do it later", "Resolved" (case-insensitive).
     *
     * @param pgId         the unique identifier of the PG for which complaints are to be retrieved
     * @param statusFilter the status filter ("All", "Pending action", "Work in progress", "Do it later", "Resolved")
     * @return a list of ComplaintResponseDto objects representing filtered complaints for the specified PG
     */
    @Transactional(readOnly = true)
    @Override
    public List<ComplaintResponseDto> getAllComplaintsByPg(long pgId, String statusFilter) {
        List<ComplaintEntity> complaintEntities;
        if (statusFilter == null || statusFilter.equalsIgnoreCase("All complaints") || statusFilter.isEmpty()) {
            complaintEntities = complaintRepository.findByPg(pgId);
        } else {
            ComplaintStatus status = switch (statusFilter.trim().toLowerCase()) {
                case "pending action" -> ComplaintStatus.PENDING;
                case "work in progress" -> ComplaintStatus.WORK_IN_PROGRESS;
                case "do it later" -> ComplaintStatus.DO_IT_LATER;
                case "resolved" -> ComplaintStatus.RESOLVED;
                default -> throw new IllegalArgumentException("Invalid status filter: " + statusFilter);
            };
            complaintEntities = complaintRepository.findByPgAndStatus(pgId, status);
        }
        List<ComplaintResponseDto> dtos = new ArrayList<>();
        for (ComplaintEntity complaintEntity : complaintEntities) {
            ComplaintResponseDto dto = getComplaintResponseDto(complaintEntity);
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * Updates the status of a complaint by its unique identifier.
     * <p>
     * This method retrieves the ComplaintEntity by its ID, validates the new status string by converting it to the
     * ComplaintStatus enum, and updates the complaint's status. If the new status is RESOLVED, it also sets the
     * resolvedAt date to the current date. The updated complaint is then saved to the database. If the complaint
     * does not exist, a ResourceNotFoundException is thrown. If the provided status string is invalid, an
     * IllegalArgumentException is thrown. This operation is transactional to ensure data consistency.
     *
     * @param complaintId the unique identifier of the complaint to update
     * @param status      the new status to set (e.g., "PENDING", "RESOLVED")
     * @throws ResourceNotFoundException if the complaint with the given ID does not exist
     * @throws ResourceNotFoundException if the provided status string is not a valid ComplaintStatus
     */
    @Transactional
    @Override
    public void updateComplaintStatus(long complaintId, String status) {
        ComplaintEntity complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));
        ComplaintStatus newStatus;
        try {
            newStatus = ComplaintStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid Complaint status");
        }
        complaint.setStatus(newStatus);
        if (newStatus == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDate.now());
        }
        complaintRepository.save(complaint);
    }

    /**
     * Retrieves complaints for a tenant by PG ID, tenant ID, and optional status.
     * <p>
     * This method fetches ComplaintEntity records associated with the specified PG ID and tenant ID.
     * If a status is provided, only complaints matching the status are retrieved. The method constructs
     * a list of ComplaintResponseDto objects from the retrieved complaints, similar to other retrieval methods.
     * This allows for filtering complaints based on their status in addition to PG and tenant identifiers.
     *
     * @param pgId     the ID of the PG accommodation
     * @param tenantId the ID of the tenant
     * @param status   optional status to filter complaints (e.g., "PENDING", "RESOLVED")
     * @return a list of ComplaintResponseDto objects matching the criteria
     */
    @Transactional(readOnly = true)
    @Override
    public List<ComplaintResponseDto> getComplaintsByTenantAndStatus(Long pgId, Long tenantId, String status) {
        List<ComplaintEntity> complaintEntities;
        if (status != null && !status.isEmpty()) {
            complaintEntities = complaintRepository.findByPgIdAndTenantIdAndStatus(pgId, tenantId, ComplaintStatus.valueOf(status.toUpperCase()));
        } else {
            complaintEntities = complaintRepository.findByPgIdAndTenantId(pgId, tenantId);
        }
        List<ComplaintResponseDto> responseDtos = new ArrayList<>();
        for (ComplaintEntity complaintEntity : complaintEntities) {
            ComplaintResponseDto dto = getComplaintResponseDto(complaintEntity);
            responseDtos.add(dto);
        }
        return responseDtos;
    }

    private record Result(TenantInfoEntity tenant, PgManagementEntity pg) {
    }
}
