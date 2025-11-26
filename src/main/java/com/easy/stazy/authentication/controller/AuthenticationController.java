package com.easy.stazy.authentication.controller;


import com.easy.stazy.pgmanagement.tenant.dto.response.TenantPgsHistoryResponseDto;
import com.easy.stazy.pgmanagement.tenant.service.TenantInfoService;
import com.easy.stazy.shared.common.exception.InvalidRequestException;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import com.easy.stazy.authentication.dto.RoleType;
import com.easy.stazy.authentication.dto.request.LoginRequest;
import com.easy.stazy.authentication.dto.response.AuthResponse;
import com.easy.stazy.authentication.dto.response.LoginResponse;
import com.easy.stazy.authentication.service.UserService;
import com.google.i18n.phonenumbers.NumberParseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
@Slf4j
@Tag(name = "Authentication Controller", description = "APIs for user authentication and authorization")
public class AuthenticationController {
    private final UserService userService;

    private final TenantInfoService tenantInfoService;


    @Operation(summary = "Login as Tenant", description = "Sends OTP to the tenant's phone number for login.")
    @PostMapping("/login/tenant")
    public ResponseEntity<SuccessResponseBody> login(@Valid @Pattern(regexp = "^(\\+91[\\-\\s]?|0)?[6-9]\\d{9}$", message = "Invalid phone number") @RequestParam String phoneNumber) {
        try {
            LoginResponse response = userService.login(phoneNumber, RoleType.TENANT.toString());
            SuccessResponseBody responseBody = new SuccessResponseBody("OTP sent successfully", response);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            throw new InvalidRequestException("Invalid login request");
        }
    }

    @Operation(summary = "Login as Owner", description = "Sends OTP to the owner's phone number for login.")
    @PostMapping("/login/owner")
    public ResponseEntity<SuccessResponseBody> loginOwner(@Valid @Pattern(regexp = "^(\\+91[\\-\\s]?|0)?[6-9]\\d{9}$", message = "Invalid phone number") @RequestParam String phoneNumber) throws NumberParseException {
        log.info("Owner login request received for phone number: {}", phoneNumber);
        LoginResponse response = userService.login(phoneNumber, RoleType.OWNER.toString());
        SuccessResponseBody responseBody = new SuccessResponseBody("OTP sent successfully", response);
        log.info("OTP sent successfully to owner with phone number: {}", phoneNumber);
        return ResponseEntity.ok(responseBody);
    }

    @Operation(summary = "Logout User", description = "Logs out the user by invalidating their session/token.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Verifies OTP for OWNER login and returns AuthResponse.
     */
    @Operation(summary = "Verify OTP for Owner", description = "Verifies the OTP sent to the owner's phone number and generates an authentication token.")
    @PostMapping("/verify-otp/owner")
    public ResponseEntity<SuccessResponseBody> verifyOtpOwner(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.verifyOwnerOtpAndGenerateToken(request);
        if (response == null) {
            return ResponseEntity.badRequest().body(new SuccessResponseBody("The OTP you entered is incorrect or the account does not exist. Please check and try again.", null));
        }
        SuccessResponseBody responseBody = new SuccessResponseBody("OTP verified successfully", response);
        return ResponseEntity.ok(responseBody);
    }

    /**
     * Verifies OTP for TENANT login, returns AuthResponse and tenant PGs history.
     */
    @Operation(summary = "Verify OTP for Tenant", description = "Verifies the OTP sent to the tenant's phone number and generates an authentication token along with tenant PGs history.")
    @PostMapping("/verify-otp/tenant")
    public ResponseEntity<SuccessResponseBody> verifyOtpTenant(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.verifyTenantOtpAndGenerateToken(request);
        if (response == null) {
            return ResponseEntity.badRequest().body(new SuccessResponseBody("The OTP you entered is incorrect or the account does not exist. Please check and try again.", null));
        }
        List<TenantPgsHistoryResponseDto> tenantPgsHistory = tenantInfoService.getTenantPgsHistoryForUser(response.getUserId());
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("authResponse", response);
        responseMap.put("tenantPgsHistory", tenantPgsHistory);
        SuccessResponseBody responseBody = new SuccessResponseBody("OTP verified successfully", responseMap);
        return ResponseEntity.ok(responseBody);
    }

    @Operation(summary = "Refresh Token", description = "Generates a new authentication token using the provided refresh token.")
    @PostMapping("/refreshtoken")
    public ResponseEntity<SuccessResponseBody> generateToken(@Valid @RequestParam String refreshToken) {
        AuthResponse response = userService.refreshToken(refreshToken);
        SuccessResponseBody responseBody = new SuccessResponseBody("RefreshToken created Successfully", response);
        return ResponseEntity.ok(responseBody);
    }


}
