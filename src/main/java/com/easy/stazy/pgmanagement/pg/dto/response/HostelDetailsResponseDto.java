package com.easy.stazy.pgmanagement.pg.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@Builder
public class HostelDetailsResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 5463307419570128367L;

    private String pgName;
    private String ownerName;
    private String mobileNumber;
    private String pgManagerName;
    private String pgManagerContactNumber;
    private String pgManagerAdditionalContactNumber;
    private String state;
    private String city;
    private String locality;
    private String pincode;
    private String description;

}
