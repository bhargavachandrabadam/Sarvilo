package com.easy.stazy.pgmanagement.admin.service.handler;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsResponseDto;

public interface PgFieldHandler {
    void handle(PgManagementEntity entity, PgDetailsResponseDto dto);

    void update(PgManagementRequestDto dto, PgManagementEntity entity);
}

