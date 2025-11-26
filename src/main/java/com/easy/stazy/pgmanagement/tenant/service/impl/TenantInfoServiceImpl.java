package com.easy.stazy.pgmanagement.tenant.service.impl;

import com.easy.stazy.authentication.dto.RoleType;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.authentication.repository.UserRepository;
import com.easy.stazy.authentication.service.UserService;
import com.easy.stazy.bookings.entity.BookingRequestEntity;
import com.easy.stazy.bookings.entity.BookingRequestPhotoProofEntity;
import com.easy.stazy.favourites.repository.FavoriteRepository;
import com.easy.stazy.pgmanagement.admin.mapper.PgResponseMapper;
import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsWithPhotosResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgUserListResponseDto;
import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.entities.RentalOptionEntity;
import com.easy.stazy.pgmanagement.pg.enums.RentalType;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import com.easy.stazy.pgmanagement.pg.repository.RentalOptionRepository;
import com.easy.stazy.pgmanagement.pg.service.PgManagementService;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantCreateRequestDto;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantInfoResponseDto;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantInfoUpdateRequestDto;
import com.easy.stazy.pgmanagement.tenant.dto.response.TenantPgsHistoryResponseDto;
import com.easy.stazy.pgmanagement.tenant.dto.response.TenantProofResponseDto;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.pgmanagement.tenant.entities.TenantProofPhotoEntity;
import com.easy.stazy.pgmanagement.tenant.handlers.TenantPhotosHandler;
import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;
import com.easy.stazy.pgmanagement.tenant.service.TenantInfoService;
import com.easy.stazy.photos.PhotoPathUtilsService;
import com.easy.stazy.shared.common.constants.constants;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import com.easy.stazy.shared.service.BedAssignmentService;
import com.google.i18n.phonenumbers.NumberParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantInfoServiceImpl implements TenantInfoService {

    private final TenantInfoRepository tenantInfoRepository;
    private final UserRepository userRepository;
    private final PgResponseMapper pgResponseMapper;
    private final FavoriteRepository favoriteRepository;
    private final PgManagementRepository pgManagementRepository;
    private final PhotoPathUtilsService photoPathUtilsService;
    private final TenantPhotosHandler tenantPhotosHandler;
    private final BedAssignmentService bedAssignmentService;
    private final PgManagementService pgManagementService;
    private final UserService userService;
    private final RentalOptionRepository rentalOptionRepository;

    /**
     * method getBedOccupancyEntity retrieves the current bed occupancy for a tenant. It checks if the tenant has any bed occupancies:
     * If there are any, it tries to find the first one with status "ACTIVE" (case-insensitive).
     * If none are "ACTIVE", it returns the first bed occupancy in the list.
     * If the tenant has no bed occupancies, it returns null.
     */

    private static BedOccupancyEntity getBedOccupancyEntity(TenantInfoEntity tenant) {
        return tenant.getBedOccupancies() != null && !tenant
                .getBedOccupancies().isEmpty() ? tenant.getBedOccupancies().stream()
                .filter(b -> constants.ACTIVE.equalsIgnoreCase(b.getStatus()))
                .findFirst()
                .orElse(tenant.getBedOccupancies().get(0)) : null;
    }

    @Override
    public List<TenantInfoResponseDto> getTenantInfoByPgId(Long pgId) {
        List<Object[]> rows = tenantInfoRepository.findTenantInfoByPgIdNative(pgId);
        List<TenantInfoResponseDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            TenantInfoResponseDto dto = new TenantInfoResponseDto();
            dto.setTenantName((String) row[0]);
            dto.setContactNumber((String) row[1]);
            dto.setEmail((String) row[2]);
            dto.setFloor(row[3] != null ? row[3].toString() : null);
            dto.setRoomNo(row[4] != null ? row[4].toString() : null);
            result.add(dto);
        }
        return result;
    }

    /**
     * Retrieves a TenantInfoEntity by its identifier.
     * Throws IllegalArgumentException if the tenant does not exist.
     *
     * @param tenantId the tenant id to look up
     * @return the TenantInfoEntity for the given id
     * @throws IllegalArgumentException if no tenant is found
     */
    private TenantInfoEntity getTenantById(Long tenantId) {
        return tenantInfoRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
    }

    /**
     *
     * The `updateUserFields` method updates a `UsersEntity` object with new values from a
     * `TenantInfoUpdateRequestDto` if any of the user's name, phone number, or email has changed.
     * It checks each field, and if a change is detected, updates the field and marks `userChanged` as `true`.
     * If any field was updated, it saves the user entity using the `userRepository`.
     * This ensures only changed user data is persisted to the database.
     */
    private void updateUserFields(UsersEntity user, TenantInfoUpdateRequestDto dto) {
        boolean userChanged = false;
        if (dto.getName() != null && !dto.getName().equals(user.getName())) {
            user.setName(dto.getName());
            userChanged = true;
        }
        if (dto.getContactNumber() != null && !dto.getContactNumber().equals(user.getPhoneNumber())) {
            user.setPhoneNumber(dto.getContactNumber());
            userChanged = true;
        }
        if (dto.getEmailId() != null && !dto.getEmailId().equals(user.getEmailId())) {
            user.setEmailId(dto.getEmailId());
            userChanged = true;
        }
        if (userChanged) {
            userRepository.save(user);
        }
    }

    /**
     *
     * The getBedEntity method retrieves a BedEntity object based on floor number, room number, bed number, and PG (Paying Guest) ID.
     * It uses the bedRepository to search for a bed matching these details.
     * If no such bed is found, it throws an IllegalArgumentException with the message
     * "Bed not found for given details". Otherwise, it returns the found BedEntity.
     */
    private BedEntity getOrCreateBedEntity(TenantInfoUpdateRequestDto dto, PgManagementEntity pg) {
        return bedAssignmentService.getOrCreateBedEntity(pg.getId(), dto.getFloorNumber(), dto.getRoomNumber(), dto.getBedNumber(), dto.getRentalType(), dto.getSharingType());
    }

    /**
     * the createTenant method orchestrates tenant onboarding within a single database transaction.
     * <p>
     * <p>
     * It is annotated with @Transactional, so all steps commit or roll back together if an exception is thrown.
     * It retrieves an existing user with TENANT role by phone, or creates a new one if none exists (getUsersEntity).
     * It resolves the requested bed by floor, room, bed number, and PG id, and ensures the bed exists and is not already occupied
     * (getBedEntity for the create flow validates availability).
     * It creates and saves a TenantInfoEntity linked to the user (getTenantInfoEntity).
     * It creates an ACTIVE BedOccupancyEntity for the tenant with the current date (createBedOccupancy).
     * It returns a TenantInfoResponseDto populated from the saved entities (tenant’s name, contact, email, floor, room, and ID proof type).
     *
     */
    @Override
    @Transactional
    public void createTenant(TenantCreateRequestDto dto, MultipartFile profilePhoto, Long pgId) {
        UsersEntity user = getUsersEntity(dto);
        TenantInfoEntity tenant = tenantInfoRepository.findByUserIdAndPgId(user.getId(), pgId)
                .orElseGet(() -> getTenantInfoEntity(user));
        BedEntity bed = bedAssignmentService.getOrCreateBedEntity(pgId, dto.getFloorNumber(), dto.getRoomNumber(), dto.getBedNumber(), dto.getRentalType().name(), dto.getSharingType());
        PgManagementEntity pgManagementEntity = pgManagementRepository.findById(pgId)
                .orElseThrow(() -> new ResourceNotFoundException("PG not found for given id"));
        tenant.setPg(pgManagementEntity);
        tenantPhotosHandler.handleProfilePhoto(tenant, profilePhoto);
        tenantPhotosHandler.uploadProofPhotos(tenant, dto.getTenantProofs());
        tenantInfoRepository.save(tenant);
        // Create bed occupancy for the tenant
        bedAssignmentService.createBedOccupancy(bed, tenant, dto.getRentalType().name(), dto.getSharingType());
    }

    @Override
    @Transactional
    public void updateTenant(Long tenantId, TenantInfoUpdateRequestDto dto, MultipartFile profilePhoto) {
        TenantInfoEntity tenant = getTenantById(tenantId);
        updateUserFields(tenant.getUser(), dto);
        // Bed update logic
        if (dto.getFloorNumber() != null && dto.getRoomNumber() != null && dto.getBedNumber() != null) {
            BedOccupancyEntity oldBedOcc = getBedOccupancyEntity(tenant);
            bedAssignmentService.vacateBedOccupancy(oldBedOcc);
            PgManagementEntity pg = tenant.getPg();
            BedEntity newBed = bedAssignmentService.getOrCreateBedEntity(pg.getId(), dto.getFloorNumber(), dto.getRoomNumber(), dto.getBedNumber(), dto.getRentalType(), dto.getSharingType());
            bedAssignmentService.createBedOccupancy(newBed, tenant, dto.getRentalType(), dto.getSharingType());
        }
        tenantPhotosHandler.handleProfilePhoto(tenant, profilePhoto);
        tenantPhotosHandler.uploadProofPhotos(tenant, dto.getTenantProofs());
        tenantInfoRepository.save(tenant);
    }

    /**
     * Builds and persists a TenantInfoEntity linked to the provided user using data
     * from the request.
     *
     * @param user the already resolved or newly created user
     * @return the saved TenantInfoEntity
     */
    private TenantInfoEntity getTenantInfoEntity(UsersEntity user) {
        TenantInfoEntity tenant = new TenantInfoEntity();
        tenant.setUser(user);
        tenant = tenantInfoRepository.save(tenant);
        return tenant;
    }

    /**
     * Resolves a tenant user by phone number and TENANT role, creating one if not present.
     * The created user is initialized with name, email, active status and TENANT role.
     *
     * @param dto the incoming tenant creation request
     * @return the existing or newly created UsersEntity with TENANT role
     */
    private UsersEntity getUsersEntity(TenantCreateRequestDto dto) {
        return userRepository.findByPhoneNumberAndRole(dto.getContactNumber(), RoleType.TENANT.name())
                .orElseGet(() -> {
                    try {
                        return createUser(dto);
                    } catch (NumberParseException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Creates a new user entity for a tenant using the provided DTO.
     *
     * @param dto the tenant creation request
     * @return the newly created UsersEntity
     */
    private UsersEntity createUser(TenantCreateRequestDto dto) throws NumberParseException {
        return userService.createUser(dto.getContactNumber(), UserType.TENANT.name(), dto.getName(), dto.getEmailId());
    }


    /**
     * Creates a tenant from a BookingRequestEntity after booking approval, migrating proof photos.
     *
     * @param booking The approved BookingRequestEntity
     */
    @Override
    public void createTenantAfterApproval(BookingRequestEntity booking) {
        TenantInfoEntity tenant = new TenantInfoEntity();
        UsersEntity user = booking.getUser();
        tenant.setUser(user);
        tenant.setPg(booking.getPg());
        // Set other fields as needed
        tenant = tenantInfoRepository.save(tenant);
        // Migrate proof photos from booking to tenant
        if (booking.getProofPhotos() != null) {
            if (tenant.getProofPhotos() == null) {
                tenant.setProofPhotos(new ArrayList<>());
            }
            for (BookingRequestPhotoProofEntity proof : booking.getProofPhotos()) {
                TenantProofPhotoEntity tenantProof = new TenantProofPhotoEntity();
                tenantProof.setTenant(tenant);
                tenantProof.setProofType(proof.getProofType());
                // Use TenantPhotosHandler to move file from booking-request-photos to tenant folder
                String oldUrl = proof.getProofPhotoUrl();
                String newUrl = tenantPhotosHandler.moveProofPhotoToTenantFolder(oldUrl, tenant.getId());
                tenantProof.setProofPhotoUrl(newUrl);
                tenant.getProofPhotos().add(tenantProof);
            }
            // Move tenant profile photo if present
            if (booking.getTenantPhotoUrl() != null) {
                String newProfileUrl = tenantPhotosHandler.moveProfilePhotoToTenantFolder(booking.getTenantPhotoUrl(), tenant.getId());
                tenant.setTenantPhotoUrl(newProfileUrl);
            }
            tenantInfoRepository.save(tenant);
        }
    }

    /**
     * Get list of PGs for a user with favorite status and details.
     * Only accessible for users with USER role.
     */
    @Override
    public List<PgUserListResponseDto> getPgsListForUser(Long userId) {
        List<TenantInfoEntity> tenantInfos = tenantInfoRepository.findAllByUserId(userId);
        List<Long> favoritePgIds = favoriteRepository.findPgIdsByUserId(userId);
        List<PgUserListResponseDto> result = new ArrayList<>();
        for (TenantInfoEntity tenant : tenantInfos) {
            BedOccupancyEntity bedOccupancy = tenant.getBedOccupancies() != null && !tenant.getBedOccupancies().isEmpty()
                    ? tenant.getBedOccupancies().stream().filter(b -> "ACTIVE".equalsIgnoreCase(b.getStatus())).findFirst().orElse(tenant.getBedOccupancies().get(0))
                    : null;
            PgManagementEntity pg = null;
            if (bedOccupancy != null && bedOccupancy.getBed() != null && bedOccupancy.getBed().getRoom() != null && bedOccupancy.getBed().getRoom().getFloor() != null) {
                pg = bedOccupancy.getBed().getRoom().getFloor().getPg();
            }
            if (pg != null) {
                PgUserListResponseDto dto = pgResponseMapper.mapToUserListDto(pg);
                dto.setIsFavorite(favoritePgIds.contains(pg.getId()));
                dto.setTenantId(tenant.getId());
                dto.setStatus(bedOccupancy.getStatus());
                dto.setBedOccupancyId(bedOccupancy.getId());
                result.add(dto);
            }
        }
        return result;
    }

    @Override
    public TenantInfoResponseDto getTenantInfoById(Long tenantId) {
        TenantInfoEntity tenant = getTenantById(tenantId);
        UsersEntity user = tenant.getUser();
        TenantInfoResponseDto dto = new TenantInfoResponseDto();
        dto.setTenantId(tenant.getId());
        dto.setTenantName(user.getName());
        dto.setContactNumber(user.getPhoneNumber());
        dto.setEmail(user.getEmailId());
        dto.setRentalType(tenant.getBedOccupancies()
                .stream().filter(b -> constants.ACTIVE.equalsIgnoreCase(b.getStatus()))
                .findFirst()
                .map(BedOccupancyEntity::getRentalType).orElse(null));
        dto.setJoiningDate(String.valueOf(tenant.getBedOccupancies()
                .stream().filter(b -> constants.ACTIVE.equalsIgnoreCase(b.getStatus()))
                .findFirst()
                .map(BedOccupancyEntity::getJoiningDate).orElse(null)));
        dto.setBedNo(tenant.getBedOccupancies().stream()
                .filter(b -> constants.ACTIVE.equalsIgnoreCase(b.getStatus()))
                .findFirst()
                .map(b -> b.getBed().getBedNumber())
                .orElse(null)
        );
        dto.setFloor(tenant.getBedOccupancies().stream()
                .filter(b -> constants.ACTIVE.equalsIgnoreCase(b.getStatus()))
                .findFirst()
                .map(b -> b.getBed().getRoom().getFloor().getFloorNumber())
                .orElse(null));
        dto.setRoomNo(tenant.getBedOccupancies().stream()
                .filter(b -> constants.ACTIVE.equalsIgnoreCase(b.getStatus()))
                .findFirst()
                .map(b -> b.getBed().getRoom().getRoomNumber())
                .orElse(null)
        );
        List<TenantProofResponseDto> proofDtos = new ArrayList<>();
        if (tenant.getProofPhotos() != null) {
            for (TenantProofPhotoEntity proof : tenant.getProofPhotos()) {
                String proofPhotoUrl = photoPathUtilsService.toFullPhotoUrls(List.of(proof.getProofPhotoUrl())).stream().findFirst().orElse(null);
                proofDtos.add(new TenantProofResponseDto(
                        proof.getProofType(),
                        proofPhotoUrl
                ));
            }
        }
        dto.setTenantIdProofs((List) proofDtos);
        String profilePhotos = tenant.getTenantPhotoUrl();// Suppress unchecked warning for assignment
        if (profilePhotos != null) {
            String profilePhotoUrl = String.valueOf(photoPathUtilsService.toFullPhotoUrls(List.of(profilePhotos)));
            dto.setProfilePhoto(profilePhotoUrl);
        }
        return dto;
    }

    /**
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    @Override
    public List<TenantPgsHistoryResponseDto> getTenantPgsHistoryForUser(Long userId) {
        log.info("Fetching tenant PGs history for userId: {}", userId);
        List<TenantInfoEntity> tenantInfos = tenantInfoRepository.findAllByUserId(userId);
        List<TenantPgsHistoryResponseDto> result = new ArrayList<>();
        for (TenantInfoEntity tenant : tenantInfos) {
            log.info("Processing tenantId: {}", tenant.getId());
            if (tenant.getBedOccupancies() != null && !tenant.getBedOccupancies().isEmpty()) {
                for (BedOccupancyEntity bedOccupancy : tenant.getBedOccupancies()) {
                    log.info("Processing bedOccupancyId: {} with status: {}", bedOccupancy.getId(), bedOccupancy.getStatus());
                    if ("ACTIVE".equalsIgnoreCase(bedOccupancy.getStatus()) || "VACATED".equalsIgnoreCase(bedOccupancy.getStatus())) {
                        PgManagementEntity pg = null;
                        if (bedOccupancy.getBed() != null && bedOccupancy.getBed().getRoom() != null && bedOccupancy.getBed().getRoom().getFloor() != null) {
                            pg = bedOccupancy.getBed().getRoom().getFloor().getPg();
                        }
                        if (pg != null) {
                            log.info("Found PG with id: {} for bedOccupancyId: {}", pg.getId(), bedOccupancy.getId());
                            PgUserListResponseDto userListDto = pgResponseMapper.mapToUserListDto(pg);
                            userListDto.setTenantId(tenant.getId());
                            userListDto.setStatus(bedOccupancy.getStatus());
                            userListDto.setBedOccupancyId(bedOccupancy.getId());
                            userListDto.setRentalType(bedOccupancy.getRentalType());
                            userListDto.setSharingType(bedOccupancy.getSharingType());
                            RentalOptionEntity rentalAmount = rentalOptionRepository.findByPgIdAndSharingTypeAndDurationType(pg.getId(), bedOccupancy.getSharingType(), RentalType.valueOf(bedOccupancy.getRentalType()));
                            userListDto.setRentAmount(rentalAmount.getPrice());
                            PgDetailsWithPhotosResponseDto detailsDto = pgManagementService.getPgDetailsById(pg.getId());
                            log.info("Mapped PgDetailsWithPhotosResponseDto for PG id: {}", pg.getId());
                            TenantPgsHistoryResponseDto historyDto = new TenantPgsHistoryResponseDto(userListDto, detailsDto);
                            result.add(historyDto);
                        }
                    }
                }
            }
        }
        log.info("Completed fetching tenant PGs history for userId: {}. Total records: {}", userId, result.size());
        return result;
    }
}
