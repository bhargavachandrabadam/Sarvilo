package com.easy.stazy.shared.service;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.authentication.dto.TokenDto;
import com.easy.stazy.authentication.entities.UsersEntity;

public interface TokenService {
    TokenDto generateToken(UsersEntity user);

    TokenDto generateOwnerToken(PgOwnerEntity owner);
}

