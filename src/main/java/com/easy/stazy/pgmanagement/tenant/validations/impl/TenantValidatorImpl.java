package com.easy.stazy.pgmanagement.tenant.validations.impl;

import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;
import com.easy.stazy.pgmanagement.tenant.validations.TenantValidator;
import org.springframework.stereotype.Service;

@Service
public class TenantValidatorImpl implements TenantValidator {
    private final TenantInfoRepository tenantInfoRepository;

    public TenantValidatorImpl(TenantInfoRepository tenantInfoRepository) {
        this.tenantInfoRepository = tenantInfoRepository;
    }

    @Override
    public boolean validateNotAlreadyTenant(Long userId, Long pgId) {
        return tenantInfoRepository.existsByUser_IdAndPg_Id(userId, pgId);
    }
}

