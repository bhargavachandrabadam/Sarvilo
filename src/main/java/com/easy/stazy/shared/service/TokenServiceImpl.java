package com.easy.stazy.shared.service;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.authentication.dto.TokenDto;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.shared.common.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private final JwtUtils jwtUtils;

    @Override
    public TokenDto generateToken(UsersEntity user) {
        // Delegate to JwtUtils or your actual token generation logic
        return jwtUtils.generateToken(user);
    }

    @Override
    public TokenDto generateOwnerToken(PgOwnerEntity owner) {
        return jwtUtils.generateOwnerToken(owner);
    }


}

