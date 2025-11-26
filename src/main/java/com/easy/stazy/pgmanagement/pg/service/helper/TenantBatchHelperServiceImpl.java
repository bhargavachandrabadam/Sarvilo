package com.easy.stazy.pgmanagement.pg.service.helper;

import com.easy.stazy.pgmanagement.tenant.dto.request.TenantCreateRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.BedEntity;
import com.easy.stazy.pgmanagement.pg.entities.BedOccupancyEntity;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;
import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.tenant.handlers.TenantPhotosHandler;
import com.easy.stazy.shared.service.BedAssignmentService;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import com.easy.stazy.authentication.repository.UserRepository;
import com.easy.stazy.authentication.dto.RoleType;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import com.easy.stazy.authentication.service.UserService;
import com.google.i18n.phonenumbers.NumberParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantBatchHelperServiceImpl implements TenantBatchHelperService {
    private final TenantInfoRepository tenantInfoRepository;
    private final PgManagementRepository pgManagementRepository;
    private final TenantPhotosHandler tenantPhotosHandler;
    private final BedAssignmentService bedAssignmentService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void createListOfTenants(List<TenantCreateRequestDto> tenantCreateRequestDtos, List<MultipartFile> profilePhotos, Long pgId) {
        List<TenantInfoEntity> tenantsToSave = new ArrayList<>();
        List<BedOccupancyEntity> occupanciesToSave = new ArrayList<>();
        for (int i = 0; i < tenantCreateRequestDtos.size(); i++) {
            TenantCreateRequestDto dto = tenantCreateRequestDtos.get(i);
            MultipartFile profilePhoto = (profilePhotos != null && profilePhotos.size() > i) ? profilePhotos.get(i) : null;

            // Create or get user
            UsersEntity user = userRepository.findByPhoneNumberAndRole(dto.getContactNumber(), RoleType.TENANT.name())
                    .orElseGet(() -> {
                        try {
                            return userService.createUser(dto.getContactNumber(), UserType.TENANT.name(), dto.getName(), dto.getEmailId());
                        } catch (NumberParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
            TenantInfoEntity tenant = tenantInfoRepository.findByUserIdAndPgId(user.getId(), pgId)
                    .orElseGet(() -> {
                        TenantInfoEntity t = new TenantInfoEntity();
                        t.setUser(user);
                        return tenantInfoRepository.save(t);
                    });
            PgManagementEntity pgManagementEntity = pgManagementRepository.findById(pgId)
                    .orElseThrow(() -> new ResourceNotFoundException("PG not found for given id"));
            tenant.setPg(pgManagementEntity);
            tenantPhotosHandler.handleProfilePhoto(tenant, profilePhoto);
            tenantPhotosHandler.uploadProofPhotos(tenant, dto.getTenantProofs());
            tenantsToSave.add(tenant);

            // Bed assignment
            BedEntity bed = bedAssignmentService.getOrCreateBedEntity(pgId, dto.getFloorNumber(), dto.getRoomNumber(), dto.getBedNumber(), dto.getRentalType().name(), dto.getSharingType());
            BedOccupancyEntity occupancy = bedAssignmentService.createAndReturnBedOccupancy(bed, tenant, dto.getRentalType().name(), dto.getSharingType());
            occupanciesToSave.add(occupancy);
        }
        // Batch save tenants and occupancies
        tenantInfoRepository.saveAll(tenantsToSave);
        bedAssignmentService.saveAllBedOccupancies(occupanciesToSave);
    }
}

