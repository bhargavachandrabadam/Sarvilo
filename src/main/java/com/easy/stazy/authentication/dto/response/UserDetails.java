package com.easy.stazy.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class UserDetails {
    private Long userId;
    private String username;
    private Boolean isActive;
    private String role;
}
