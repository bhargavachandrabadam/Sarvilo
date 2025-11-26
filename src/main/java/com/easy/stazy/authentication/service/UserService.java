package com.easy.stazy.authentication.service;


import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.authentication.dto.request.LoginRequest;
import com.easy.stazy.authentication.dto.response.AuthResponse;
import com.easy.stazy.authentication.dto.response.LoginResponse;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.google.i18n.phonenumbers.NumberParseException;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    LoginResponse login(String mobileNumber, String roleType) throws NumberParseException;

    AuthResponse verifyOwnerOtpAndGenerateToken(LoginRequest loginRequest);

    AuthResponse verifyTenantOtpAndGenerateToken(LoginRequest loginRequest);

    void logout(HttpServletRequest request);

    AuthResponse refreshToken(String refreshToken);

    UsersEntity createUser(String mobileNumber, String roleType, String name, String emailId) throws NumberParseException;

    PgOwnerEntity createPgOwner(String ownerName, String ownerContactNumber, String ownerEmailAddress, String profilePictureUrl) throws NumberParseException;
}
