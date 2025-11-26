package com.easy.stazy.pgmanagement.tenant.validations;

import com.easy.stazy.authentication.entities.UsersEntity;


public interface UserValidationService {
    UsersEntity validateAndGetUser(Long userId);
}

