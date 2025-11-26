package com.easy.stazy.pgmanagement.pg.service.helper;

import com.easy.stazy.pgmanagement.tenant.dto.request.TenantCreateRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TenantBatchHelperService {
    void createListOfTenants(List<TenantCreateRequestDto> tenantCreateRequestDtos, List<MultipartFile> profilePhotos, Long pgId);
}

