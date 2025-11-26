package com.easy.stazy.authentication.dto.request;

import jakarta.validation.constraints.Pattern;

public record LoginRequest(@Pattern(regexp = "^(\\+91)?[0-9]{10}$", message = "Invalid phone number") String phoneNumber, String otp) {
}
