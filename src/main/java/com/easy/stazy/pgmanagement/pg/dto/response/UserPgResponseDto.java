package com.easy.stazy.pgmanagement.pg.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPgResponseDto {
    private Long pgId;
    private PgFilterResponseDto pgDetails;
    private Long tenantId;
}

