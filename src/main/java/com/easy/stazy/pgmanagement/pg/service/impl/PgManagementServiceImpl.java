package com.easy.stazy.pgmanagement.pg.service.impl;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import com.easy.stazy.pgmanagement.admin.service.handler.PgAmenitiesHandler;
import com.easy.stazy.pgmanagement.admin.service.handler.PgRentalOptionsHandler;
import com.easy.stazy.pgmanagement.admin.service.handler.PgRulesHandler;
import com.easy.stazy.pgmanagement.admin.service.handler.PgSimpleFieldsHandler;
import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsWithPhotosResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgFilterResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgOwnerProfileDto;
import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.pgmanagement.admin.service.PgAuditService;
import com.easy.stazy.pgmanagement.admin.service.PgPhotoService;
import com.easy.stazy.pgmanagement.admin.service.TenantImportService;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantCreateRequestDto;
import com.easy.stazy.pgmanagement.pg.service.helper.TenantBatchHelperService;
import com.easy.stazy.shared.common.exception.ResourceAlreadyExistsException;
import com.easy.stazy.pgmanagement.pg.service.PgManagementService;
import com.easy.stazy.authentication.service.UserService;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.pg.repository.RentalOptionRepository;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import com.easy.stazy.pgmanagement.admin.service.specification.PgSpecification;
import com.easy.stazy.pgmanagement.admin.mapper.PgResponseMapper;
import com.easy.stazy.shared.service.SecurityUserService;
import com.easy.stazy.photos.PhotoPathUtilsService;
import com.google.i18n.phonenumbers.NumberParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link PgManagementService} interface for managing PG (Paying Guest) entities.
 * <p>
 * This service provides business logic for PG management, including operations related to PG details,
 * rental options, user management, photo handling, tenant import, and security. It acts as a bridge
 * between the controller layer and the data access layer, orchestrating the use of various repositories
 * and helper services to fulfill PG-related operations.
 * <p>
 * Key responsibilities:
 * <ul>
 *   <li>Managing PG details and their persistence</li>
 *   <li>Handling rental options and related configurations</li>
 *   <li>Coordinating user and tenant import operations</li>
 *   <li>Managing PG photos and their storage</li>
 *   <li>Ensuring security and access control for PG operations</li>
 * </ul>
 * <p>
 * This class is annotated with {@code @Service} and {@code @RequiredArgsConstructor} for Spring dependency injection.
 */
@Service
@RequiredArgsConstructor
public class PgManagementServiceImpl implements PgManagementService {


    private final PgManagementRepository pgManagementRepository;

    private final RentalOptionRepository rentalOptionRepository;

    private final UserService userService;

    private final PgResponseMapper pgResponseMapper;

    private final PgPhotoService pgPhotoService;

    private final TenantImportService tenantImportService;

    private final SecurityUserService securityUserService;

    private final PgSimpleFieldsHandler pgSimpleFieldsHandler;

    private final PgAmenitiesHandler pgAmenitiesHandler;

    private final PgRulesHandler pgRulesHandler;

    private final PgRentalOptionsHandler pgRentalOptionsHandler;

    private final PgAuditService pgAuditService;

    private final PhotoPathUtilsService photoPathUtilsService;

    private final TenantBatchHelperService tenantBatchHelperService;

    private static Specification<PgManagementEntity> getPgManagementEntitySpecification(String location) {
        Specification<PgManagementEntity> spec = (root, query, cb) -> {
            if (location == null || location.isBlank()) {
                return cb.conjunction();
            }
            return cb.or(
                    cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("district")), "%" + location.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("state")), "%" + location.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("pinCode")), "%" + location.toLowerCase() + "%")
            );
        };
        return spec;
    }

    /**
     * Retrieves a list of all PG management entities.
     *
     * @return A list of PG management entities.
     */
    @Override
    public List<PgManagementEntity> getPgList() {
        return pgManagementRepository.findAll();
    }

    /**
     * Sets the current date and saves the provided PG management entity to the database.
     *
     * @param pgManagement The PG management entity to save.
     * @return The saved PG management entity.
     */
    @Override
    public PgManagementEntity addPgDetail(PgManagementEntity pgManagement) {
        pgManagement.setDate(LocalDateTime.now());
        return pgManagementRepository.save(pgManagement);
    }

    /**
     * Updates the details of an existing PG management entity by its ID.
     *
     * @param id           The ID of the PG management entity to update.
     * @param pgManagement The updated PG management entity data.
     * @return true if the update was successful, false if the entity was not found.
     */
    @Override
    public boolean updatePgDetails(Long id, PgManagementEntity pgManagement) {
        return pgManagementRepository.findById(id)
                .map(existingPg -> {
                    existingPg.setPgName(pgManagement.getPgName());
                    existingPg.setMobileNumber(pgManagement.getMobileNumber());
                    existingPg.setDate(LocalDateTime.now());
                    existingPg.setStatus("pending");
                    existingPg.setRentalOptions(pgManagement.getRentalOptions());
                    pgManagementRepository.save(existingPg);
                    return true;
                }).orElse(false);
    }

    /**
     * Creates a new PG management entity from the provided DTO, saves it, and associates amenities, rules, rental options, and photos.
     * Also creates a user for the owner.
     *
     * @param dto    The PG management request DTO containing PG details.
     * @param photos List of images to be stored for the PG.
     * @return The saved PG management entity with all associations.
     */
    @Override
    @Transactional
    public PgManagementEntity addPgDetail(PgManagementRequestDto dto, List<MultipartFile> photos) throws NumberParseException {
        PgOwnerEntity owner = userService.createPgOwner(dto.getOwnerName(), dto.getOwnerContactNumber(), dto.getOwnerEmailAddress(), null);
        String trimmedLocation = dto.getLocation() != null ? dto.getLocation().trim() : null;
        boolean exists = pgManagementRepository.existsByPgNameAndLocationAndOwner_Id(
                dto.getPgName(), trimmedLocation, owner.getId()
        );
        if (exists) {
            throw new ResourceAlreadyExistsException("PG is already created with this data");
        }
        PgManagementEntity entity = new PgManagementEntity();
        pgSimpleFieldsHandler.update(dto, entity);
        entity.setOwner(owner);
        entity.setLocation(trimmedLocation);
        PgManagementEntity savedPg = pgManagementRepository.save(entity);
        pgAmenitiesHandler.update(dto, savedPg);
        pgRulesHandler.update(dto, savedPg);
        pgRentalOptionsHandler.update(dto, savedPg);
        if (savedPg.getRentalOptions() != null) {
            savedPg.getRentalOptions().forEach(option -> option.setPg(savedPg));
        }
        if (photos != null && !photos.isEmpty()) {
            List<String> photoPaths = pgPhotoService.uploadPgPhotos(photos, savedPg.getId(), null);
            savedPg.setPhotoPaths(photoPathUtilsService.toRelativePhotoPaths(photoPaths));
            pgManagementRepository.save(savedPg);
        }
        return savedPg;
    }

    /**
     * Updates an existing PG management entity and its associations (amenities, rules, rental options) by its ID using the provided DTO.
     *
     * @param pgId The ID of the PG management entity to update.
     * @param dto  The updated PG management request DTO.
     */
    @Override
    @Transactional
    public void updatePgDetails(Long pgId, PgManagementRequestDto dto, List<MultipartFile> photos) {
        PgManagementEntity entity = pgManagementRepository.findById(pgId)
                .orElseThrow(() -> new ResourceNotFoundException("PG not found with id: " + pgId));

        pgSimpleFieldsHandler.update(dto, entity);
        pgAmenitiesHandler.update(dto, entity);
        pgRulesHandler.update(dto, entity);
        pgRentalOptionsHandler.update(dto, entity);
        pgManagementRepository.save(entity);
        pgPhotoService.handlePgPhotos(entity, photos);

    }

    /**
     * Retrieves the details of a PG management entity by its ID.
     *
     * @param pgId The ID of the PG management entity.
     * @return The PG details response DTO containing PG information.
     */
    @Override
    @Transactional(readOnly = true)
    public PgDetailsWithPhotosResponseDto getPgDetailsById(Long pgId) {
        PgManagementEntity entity = pgManagementRepository.findById(pgId)
                .orElseThrow(() -> new ResourceNotFoundException("PG not found: " + pgId));
        PgDetailsResponseDto dto = new PgDetailsResponseDto();
        pgSimpleFieldsHandler.handle(entity, dto);
        pgAmenitiesHandler.handle(entity, dto);
        pgRulesHandler.handle(entity, dto);
        pgRentalOptionsHandler.handle(entity, dto);
        PgDetailsWithPhotosResponseDto response = new PgDetailsWithPhotosResponseDto();
        response.setPgDetails(dto);
        response.setPhotoPaths(photoPathUtilsService.toFullPhotoUrls(entity.getPhotoPaths()));
        return response;
    }

    /**
     * Deletes a rental option by its ID.
     *
     * @param id The ID of the rental option to delete.
     */
    @Override
    @Transactional
    public void deleteRentalOptionById(Long id) {
        rentalOptionRepository.deleteById(id);
    }

    /**
     * Filters PGs based on the provided criteria and maps them to PG filter response DTOs.
     *
     * @param minPrice    The minimum price filter.
     * @param maxPrice    The maximum price filter.
     * @param amenities   The list of amenity filters.
     * @param sharingType The list of sharing type filters.
     * @param gender      The gender filter.
     * @param places      The list of place filters.
     * @return A list of PG filter response DTOs matching the criteria.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PgFilterResponseDto> filterPgs(Double minPrice, Double maxPrice, List<String> amenities, List<String> sharingType, String gender, List<String> places) {
        Specification<PgManagementEntity> spec = PgSpecification.filter(minPrice, maxPrice, amenities, sharingType, gender, places);
        List<PgManagementEntity> filteredPgs = pgManagementRepository.findAll(spec);
        return filteredPgs.stream()
                .map(pg -> pgResponseMapper.mapToDto(pg, minPrice, maxPrice, sharingType))
                .filter(dto -> !dto.getRentalOptions().isEmpty())
                .toList();
    }

    /**
     * Retrieves a paginated list of PG details DTOs.
     *
     * @param pageable The pagination information.
     * @return A page of PG details DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PgFilterResponseDto> getPgDetails(Pageable pageable) {
        Page<PgManagementEntity> pgPage = pgManagementRepository.findAll(pageable);
        List<PgFilterResponseDto> dtoList = pgPage.stream()
                .map(pgResponseMapper::mapToDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, pgPage.getTotalElements());
    }

    /**
     * Creates a new PG management entity from the provided DTO, saves it, and associates amenities, rules, rental options, and photos.
     * Also creates a user for the owner, imports tenant data from Excel, and stores audit information.
     *
     * @param dto    The PG management request DTO containing PG details.
     * @param photos List of images to be stored for the PG.
     * @param file   The Excel file containing tenant data.
     * @return The saved PG management entity with all associations.
     */
    @Override
    @Transactional
    public PgManagementEntity addPgDetailWithExcelAndAudit(PgManagementRequestDto dto, List<MultipartFile> photos, MultipartFile file, List<MultipartFile> profilePhotos, List<TenantCreateRequestDto> tenants) throws Exception {
        PgManagementEntity pgManagement = addPgDetail(dto, photos);
        if (tenants != null && !tenants.isEmpty()) {
            tenantBatchHelperService.createListOfTenants(tenants, profilePhotos, pgManagement.getId());
        }
        if (file != null && !file.isEmpty()) {
            tenantImportService.importTenantsAndCreateEntitiesFromExcel(file, pgManagement.getId());
            String userId = securityUserService.getCurrentUserId();
            String userName = securityUserService.getCurrentUserName();
            if ((userId != null && !userId.isEmpty()) || (userName != null && !userName.isEmpty())) {
                // pgAuditService.storeAudit(userName, userId, pgManagement.getId(), file);
                // Storing audit is commented out as dest file handling is not implemented here
            }
        }
        return pgManagement;
    }

    @Override
    public PgOwnerProfileDto getPgOwnerProfile(Long ownerId) {
        //TODO:get the details by pg or by the owner table?
        return null;
    }

    @Override
    public Page<PgFilterResponseDto> getFeaturedPgs(String location, Pageable pageable) {
        Specification<PgManagementEntity> spec = getPgManagementEntitySpecification(location);
        Page<PgManagementEntity> pgPage = pgManagementRepository.findAll(spec, pageable);
        List<PgFilterResponseDto> responseDtos = pgPage.getContent().stream()
                .map(pgResponseMapper::mapToDto)
                .sorted((a, b) -> Double.compare(b.getAverageRating() != null ? b.getAverageRating() : 0.0, a.getAverageRating() != null ? a.getAverageRating() : 0.0))
                .collect(Collectors.toList());
        return new PageImpl<>(responseDtos, pageable, pgPage.getTotalElements());
    }

}
