package com.easy.stazy.pgmanagement.admin.service;

import com.easy.stazy.pgmanagement.pg.dto.response.OwnerPgResponseDto;

import java.util.List;

public interface OwnerPgService {
    List<OwnerPgResponseDto> getPgsByOwner(Long ownerId);
}

