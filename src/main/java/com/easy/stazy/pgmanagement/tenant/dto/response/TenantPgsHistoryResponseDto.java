package com.easy.stazy.pgmanagement.tenant.dto.response;

import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsWithPhotosResponseDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgUserListResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantPgsHistoryResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 9033488358398190119L;

    private PgUserListResponseDto pgUserListResponseDto;

    private PgDetailsWithPhotosResponseDto pgDetailsWithPhotosResponseDto;

}
