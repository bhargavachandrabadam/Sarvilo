package com.easy.stazy.pgmanagement.pg.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PgFilterResponseDto {
    private Long pgId;
    private String pgName;
    private List<String> photos;
    private String address;
    private Integer reviewsCount;
    private Double averageRating;
    private List<RentalOptionDto> rentalOptions;
    private String status;

    @Data
    public static class RentalOptionDto {
        private String rentalType;
        private List<String> sharingType;
        private Double price;
        private Double cautionDeposit;
    }
}

