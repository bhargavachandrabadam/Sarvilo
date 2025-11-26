package com.easy.stazy.authentication.dto.request;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String token;
    private String password;
}
