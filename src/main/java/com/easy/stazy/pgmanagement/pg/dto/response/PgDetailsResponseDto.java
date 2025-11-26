package com.easy.stazy.pgmanagement.pg.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PgDetailsResponseDto {
    private String status;
    private String mobileNumber;
    private String pgName;
    private String location;
    private LocalDateTime date;
    private String ownerName;
    private String ownerContactNumber;
    private String ownerEmailAddress;
    private Boolean isOwnerManager;
    private String managerName;
    private String managerContactNumber;
    private String additionalContactNumber;
    private String state;
    private String district;
    private String pinCode;
    private String genderChoice;
    private String description;
    private Boolean dailyRentalActive;
    private Boolean monthlyRentalActive;
    private List<String> amenitys;
    private List<String> rules;
    private List<RentalOptionDto> rentalOptions;

    @Data
    public static class RentalOptionDto {
        private Long id;
        private String rentalType;
        private String sharingType;
        private double rent;
        private double cautionDeposit;
    }
}
