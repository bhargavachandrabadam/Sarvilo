package com.easy.stazy.pgmanagement.admin.dto.request;

import com.easy.stazy.pgmanagement.owner.validation.InAppPaymentsRequired;
import com.easy.stazy.pgmanagement.pg.dto.request.RentalOptionRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
@InAppPaymentsRequired
public class PgManagementRequestDto {
    @Schema(example = "Active")
    private String status;
    @Schema(example = "9876543210")
    private String mobileNumber;
    @Schema(example = "Sunrise PG")
    @NotBlank(message = "PG name is mandatory")
    private String pgName;
    @Schema(example = "1 Hostel Road, City, State")
    @NotBlank(message = "Location is mandatory")
    private String location;
    @Schema(example = "2025-10-23T14:30:00")
    @Pattern(regexp = "^[a-zA-Z0-9 .,:\\-T]*$", message = "Invalid characters detected")
    private String date;
    @Schema(example = "Sample Owner")
    @NotBlank(message = "Owner name is mandatory")
    private String ownerName;
    @Schema(example = "9876543210")
    @NotBlank(message = "Owner contact number is mandatory")
    private String ownerContactNumber;
    @Schema(example = "owner1@example.com")
    @Email(message = "Invalid email address")
    private String ownerEmailAddress;
    @Schema(example = "true")
    private Boolean isOwnerManager;
    @Schema(example = "Sample Owner")
    private String managerName;
    @Schema(example = "9876543210")
    @Pattern(regexp = "^[0-9+]*$", message = "Invalid manager contact number")
    private String managerContactNumber;
    @Schema(example = "9123456780")
    @Pattern(regexp = "^[0-9+]*$", message = "Invalid additional contact number")
    private String additionalContactNumber;
    @Schema(example = "Karnataka")
    @NotBlank(message = "state is mandatory")
    private String state;
    @Schema(example = "Bangalore")
    @NotBlank(message = "district is mandatory")
    private String district;
    @Schema(example = "560001")
    @NotBlank(message = "Pin code is mandatory")
    @Pattern(regexp = "^\\d{6}$", message = "Pin code must be 6 digits")
    private String pinCode;
    @Schema(example = "Male")
    @NotBlank(message = "Gender choice is mandatory")
    private String genderChoice;
    @Schema(example = "Premium PG with all amenities.")
    private String description;
    @Schema(example = "true")
    private Boolean dailyRentalActive;
    @Schema(example = "true")
    private Boolean monthlyRentalActive;
    @Schema(example = "[\"WiFi\", \"Laundry\"]")
    @NotEmpty(message = "Amenities are mandatory")
    private List<String> amenitys;
    @Schema(example = "[\"No loud music after 10 PM\", \"Visitors allowed till 8 PM\"]")
    @NotEmpty(message = "Rules are mandatory")
    private List<String> rules;
    @Schema(example = "[{\"rentalType\": \"MONTHLY\", \"sharingType\": \"Double\", \"rent\": 6000.00, \"cautionDeposit\": 1000.00}]")
    private List<RentalOptionRequestDto> rentalOptions;
    @Schema(example = "true")
    private Boolean inAppPayments;
    @Schema(example = "sample@upi")
    private String upiAddress;
    @Schema(example = "29ABCDE1234F2Z5")
    private String gstin;
    @Schema(example = "1234567890")
    private String bankAccountNumber;
    @Schema(example = "Sample Owner")
    private String accountHolderName;
    @Schema(example = "SBIN0001234")
    private String ifsc;
}
