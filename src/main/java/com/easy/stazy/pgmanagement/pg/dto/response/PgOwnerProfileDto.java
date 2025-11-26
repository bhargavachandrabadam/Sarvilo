package com.easy.stazy.pgmanagement.pg.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PgOwnerProfileDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 2143475570286543595L;

    private String name;
    private String fullName;
    private String mobileNumber;
    private String emailId;
    private String upiAddress;
    private String gstin;
    private String bankAccountNumber;
    private String accountHolderName;
    private String ifsc;
    private String profilePictureUrl;
}
