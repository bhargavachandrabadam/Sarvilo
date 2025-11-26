package com.easy.stazy.pgmanagement.pg.dto.response;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerPgResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 3514841301747588050L;
    private Long pgId;
    private String pgName;
    private String description;
    private String managerName;
    private String bookingStatus;
    private Long occupiedBeds;
    private Long totalBeds;
    private Long ratingCount;
    private Double averageRating;
    private LocationDto location;
    private List<String> floors;
    private List<YearMonth> yearMonths;
    private List<String>  photos;


    @Data
    public static class LocationDto {
        private String location;
        private String district;
        private String state;
        private String pinCode;

    }
}

