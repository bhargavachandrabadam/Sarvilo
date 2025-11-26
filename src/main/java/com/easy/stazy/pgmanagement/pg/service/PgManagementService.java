package com.easy.stazy.pgmanagement.pg.service;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsWithPhotosResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgFilterResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgOwnerProfileDto;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantCreateRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.google.i18n.phonenumbers.NumberParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PgManagementService {
    PgManagementEntity addPgDetail(PgManagementRequestDto dto, List<MultipartFile> photos) throws NumberParseException;

    PgManagementEntity addPgDetail(PgManagementEntity dto);

    List<PgManagementEntity> getPgList();

    boolean updatePgDetails(Long id, PgManagementEntity pgManagement);

    void updatePgDetails(Long id, PgManagementRequestDto dto, List<MultipartFile> photos);

    PgDetailsWithPhotosResponseDto getPgDetailsById(Long pgId);

    void deleteRentalOptionById(Long id);

    List<PgFilterResponseDto> filterPgs(Double minPrice, Double maxPrice, List<String> amenities, List<String> sharingType, String gender, List<String> places);

    Page<PgFilterResponseDto> getPgDetails(Pageable pageable);

    PgManagementEntity addPgDetailWithExcelAndAudit(PgManagementRequestDto dto, List<MultipartFile> photos, MultipartFile file, List<MultipartFile> profilePhotos, List<TenantCreateRequestDto> tenants) throws Exception;

    PgOwnerProfileDto getPgOwnerProfile(Long ownerId);

    Page<PgFilterResponseDto> getFeaturedPgs(String location, Pageable pageable);
}
