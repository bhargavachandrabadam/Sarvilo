package com.easy.stazy.authentication.service.impl;


import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.pgmanagement.admin.repository.PgOwnerRepository;
import com.easy.stazy.authentication.entities.TokenEntity;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import com.easy.stazy.shared.common.util.JwtUtils;
import com.easy.stazy.authentication.customvalidations.ValidateUser;
import com.easy.stazy.authentication.dto.RoleType;
import com.easy.stazy.authentication.dto.TokenDto;
import com.easy.stazy.authentication.dto.request.LoginRequest;
import com.easy.stazy.authentication.dto.response.AuthResponse;
import com.easy.stazy.authentication.dto.response.LoginResponse;
import com.easy.stazy.authentication.entities.OtpEntity;
import com.easy.stazy.authentication.repository.TokenRepository;
import com.easy.stazy.authentication.repository.UserRepository;
import com.easy.stazy.authentication.service.OtpService;
import com.easy.stazy.authentication.service.UserService;
import com.easy.stazy.shared.service.TokenService;
import com.easy.stazy.shared.common.util.PhoneUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserService} interface.
 * <p>
 * This service handles user authentication and OTP management for login operations.
 * It follows SOLID principles by delegating OTP generation and persistence to {@link OtpService}.
 * </p>
 * <ul>
 *   <li>Creates a new user if not present in the database.</li>
 *   <li>Delegates OTP generation, update, and persistence to {@link OtpService}.</li>
 *   <li>Returns an {@link AuthResponse} containing the OTP for further processing (e.g., sending via SMS/email).</li>
 * </ul>
 *
 * @author YourName
 * @since 2025-09-15
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final ValidateUser validateUser;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final TokenService tokenService;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;
    private final PgOwnerRepository pgOwnerRepository;


    /**
     * Authenticates a user by mobile number and role type.
     * <p>
     * If the user does not exist, a new user is created. An OTP is generated and persisted using {@link OtpService}.
     * </p>
     *
     * @param mobileNumber the user's mobile number
     * @param roleType     the user's role type (e.g., OWNER, TENANT)
     * @return {@link AuthResponse} containing the generated OTP
     */
    @Override
    public LoginResponse login(String mobileNumber, String roleType) throws NumberParseException {
        String normalizedPhoneNumber = PhoneUtil.normalizeToE164(mobileNumber, "IN");
        log.info("Login request received for mobile number: {} with role type: {}", mobileNumber, roleType);
        if (roleType.equals(RoleType.OWNER.toString())) {
            log.info("Processing login for OWNER role");
            PgOwnerEntity pgOwner = createPgOwner(null, normalizedPhoneNumber, null, null);
            String otpValue = otpService.generateAndSaveOtp(pgOwner);
            //TODO: Send OTP to Owner via SMS/Email
            log.info("OTP generated for OWNER: {}", otpValue);
            return LoginResponse.builder().otp(otpValue).build();
        } else if (roleType.equals(RoleType.TENANT.toString())) {
            log.info("Processing login for TENANT role");
            UsersEntity user = createUser(normalizedPhoneNumber, roleType, null, null);
            log.info("User retrieved/created for TENANT: {}", user.getId());
            String otpValue = otpService.generateAndSaveOtp(user);
            //TODO: Send OTP to Tenant via SMS/Email
            log.info("OTP generated");
            return LoginResponse.builder().otp(otpValue).build();
        } else {
            log.error("Invalid role type provided: {}", roleType);
            throw new IllegalArgumentException("Invalid role type. Must be either 'OWNER' or 'TENANT'.");
        }
    }

    /**
     * Retrieves an existing user by mobile number or creates a new user if not found.
     *
     * @param mobileNumber the user's mobile number
     * @param roleType     the user's role type
     * @return the existing or newly created {@link UsersEntity} entity
     */
    @Transactional
    @Override
    public UsersEntity createUser(String mobileNumber, String roleType, String name, String emailId) throws NumberParseException {
        String normalizedPhoneNumber = PhoneUtil.normalizeToE164(mobileNumber, "IN");
        UsersEntity user = userRepository.findByPhoneNumber(normalizedPhoneNumber).orElse(null);
        if (user == null) {
            // Create new user
            user = UsersEntity.builder()
                    .phoneNumber(normalizedPhoneNumber)
                    .name(name)
                    .emailId(emailId)
                    .role(RoleType.valueOf(roleType))
                    .isActive(true)
                    .build();
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public PgOwnerEntity createPgOwner(String ownerName, String ownerContactNumber, String ownerEmailAddress, String profilePictureUrl) throws NumberParseException {
        String normalizedPhoneNumber = PhoneUtil.normalizeToE164(ownerContactNumber, "IN");
        PgOwnerEntity pgOwner = pgOwnerRepository.findByOwnerContactNumber(ownerContactNumber).orElse(null);
        if (pgOwner == null) {
            pgOwner = PgOwnerEntity.builder()
                    .ownerName(ownerName != null ? ownerName.trim() : null)
                    .ownerContactNumber(ownerContactNumber.trim())
                    .ownerEmailAddress(ownerEmailAddress != null ? ownerEmailAddress.trim() : null)
                    .profilePictureUrl(profilePictureUrl)
                    .isActive(true)
                    .build();
            pgOwner = pgOwnerRepository.save(pgOwner);
        }
        return pgOwner;
    }

    /**
     * Verifies OTP and generates token for TENANT user.
     *
     * @param loginRequest LoginRequest containing phone number and OTP
     * @return AuthResponse for tenant
     */
    public AuthResponse verifyTenantOtpAndGenerateToken(LoginRequest loginRequest) {
        String phoneNumber = loginRequest.phoneNumber();
        String otp = loginRequest.otp();
        UsersEntity user;
        try {
            user = validateUser.validMobileNumber(phoneNumber);
        } catch (Exception e) {
            user = null;
        }
        if (user != null && user.getRole() != null && user.getRole().toString().equalsIgnoreCase("TENANT")) {
            OtpEntity otpEntity = otpService.getActiveOtpForUser(user);
            if (otpEntity != null && otpEntity.getOtp().equals(otp)) {
                otpService.deactivateOtp(otpEntity);
                TokenDto token = tokenService.generateToken(user);
                return AuthResponse.builder()
                        .userId(user.getId())
                        .role(user.getRole().toString())
                        .username(user.getName())
                        .phoneNumber(user.getPhoneNumber())
                        .token(token)
                        .build();
            }
        }
        return null;
    }

    /**
     * Verifies OTP and generates token for OWNER user.
     *
     * @param loginRequest LoginRequest containing phone number and OTP
     * @return AuthResponse for owner
     */
    public AuthResponse verifyOwnerOtpAndGenerateToken(LoginRequest loginRequest) {
        String phoneNumber = loginRequest.phoneNumber();
        String otp = loginRequest.otp();
        PgOwnerEntity user;
        try {
            user = validateUser.validatePgOwner(phoneNumber);
        } catch (Exception e) {
            user = null;
        }
        if (user != null) {
            OtpEntity otpEntity = otpService.getActiveOtpForPgOwner(user);
            if (otpEntity != null && otpEntity.getOtp().equals(otp)) {
                otpService.deactivateOtp(otpEntity);
                TokenDto token = tokenService.generateOwnerToken(user);
                return AuthResponse.builder()
                        .userId(user.getId())
                        .role(UserType.OWNER.toString())
                        .username(user.getOwnerName())
                        .phoneNumber(user.getOwnerContactNumber())
                        .token(token)
                        .build();
            }
        }
        return null;
    }

    @Transactional
    @Override
    public void logout(HttpServletRequest request) {
        // Invalidate all tokens for the user (delete or deactivate)
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        String token = authHeader.substring(7);
        String username = jwtUtils.getUsernameFromToken(token);
        Long userId = userRepository.findUserIdByPhoneNumber(username);
        tokenRepository.deleteAllByUserId(userId);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // Validate the refresh token
        TokenEntity tokenEntity = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("User/Owner not found"));
        UsersEntity user = null;
        PgOwnerEntity owner = null;
        if (tokenEntity.getUserId() != null) {
            user = userRepository.findById(tokenEntity.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        } else if (tokenEntity.getOwnerId() != null) {
            owner = pgOwnerRepository.findById(tokenEntity.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        } else {
            throw new ResourceNotFoundException("No user or owner associated with token");
        }
        TokenDto newToken;
        if (user != null) {
            newToken = tokenService.generateToken(user);
            return AuthResponse.builder()
                    .userId(user != null ? user.getId() : null)
                    .role(user != null && user.getRole() != null ? user.getRole().toString() : null)
                    .token(newToken)
                    .build();
        } else {
            newToken = tokenService.generateOwnerToken(owner);
            return AuthResponse.builder()
                    .userId(owner != null ? owner.getId() : null)
                    .role("OWNER")
                    .token(newToken)
                    .build();
        }
    }

}
