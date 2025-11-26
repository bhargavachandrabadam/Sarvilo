package com.easy.stazy.pgmanagement.tenant.validations;

import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.authentication.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserValidationServiceImpl implements UserValidationService {
    private final UserRepository userRepository;

    public UserValidationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UsersEntity validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

