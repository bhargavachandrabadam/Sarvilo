package com.easy.stazy.pgmanagement.tenant.service;

import com.easy.stazy.pgmanagement.pg.dto.response.PgUserListResponseDto;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantCreateRequestDto;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantInfoResponseDto;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantInfoUpdateRequestDto;
import com.easy.stazy.pgmanagement.tenant.dto.response.TenantPgsHistoryResponseDto;
import com.easy.stazy.bookings.entity.BookingRequestEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TenantInfoService {
    List<TenantInfoResponseDto> getTenantInfoByPgId(Long pgId);

    void updateTenant(Long tenantId, TenantInfoUpdateRequestDto dto, MultipartFile profilePhoto);

    void createTenant(TenantCreateRequestDto dto, MultipartFile proofPhoto, Long pgId);

    List<PgUserListResponseDto> getPgsListForUser(Long userId);

    void createTenantAfterApproval(BookingRequestEntity booking);

    TenantInfoResponseDto getTenantInfoById(Long tenantId);

    List<TenantPgsHistoryResponseDto> getTenantPgsHistoryForUser(Long userId);
}

