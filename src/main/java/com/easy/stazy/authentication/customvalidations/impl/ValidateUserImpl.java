package com.easy.stazy.authentication.customvalidations.impl;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.pgmanagement.admin.repository.PgOwnerRepository;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.shared.common.exception.ResourceNotFoundException;
import com.easy.stazy.authentication.customvalidations.ValidateUser;
import com.easy.stazy.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.easy.stazy.shared.common.constants.MessageConstants.USER_NOT_FOUND;
import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class ValidateUserImpl implements ValidateUser {

    private final UserRepository userRepository;

    private final PgOwnerRepository pgOwnerRepository;


    /**
     * @param phoneNumber
     * @return
     */
    @Override
    public UsersEntity validMobileNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException(format(USER_NOT_FOUND)));


    }

    @Override
    public PgOwnerEntity validatePgOwner(String mobileNumber) {
        return pgOwnerRepository.findByOwnerContactNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException(format(USER_NOT_FOUND)));
    }
}
