package com.easy.stazy.authentication.dto.response;

import com.easy.stazy.authentication.dto.TokenDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private Long userId;
    private String username;
    private String role;
    private String phoneNumber;
    private TokenDto token;
}