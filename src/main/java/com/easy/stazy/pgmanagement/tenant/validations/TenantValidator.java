package com.easy.stazy.pgmanagement.tenant.validations;

public interface TenantValidator {
    boolean validateNotAlreadyTenant(Long userId, Long pgId);
}

