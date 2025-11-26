package com.easy.stazy.pgmanagement.pg.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PgUserListResponseDto {
    private Long tenantId;
    private Long pgId;
    private String pgName;
    private String location;
    private String state;
    private String district;
    private String pincode;
    private Double averageRating;
    private Integer reviewsCount;
    private String managerName;
    private Long rentalOptionId;
    private String status;
    private Boolean isFavorite;
    private Long bedOccupancyId;
    private String rentalType;
    private String sharingType;
    private BigDecimal rentAmount;

    public Boolean getIsFavorite() {
        return isFavorite != null ? isFavorite : Boolean.FALSE;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
