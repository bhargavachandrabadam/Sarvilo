package com.easy.stazy.shared.security;


import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.pgmanagement.admin.repository.PgOwnerRepository;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 * This service is responsible for loading user-specific data during authentication.
 * It retrieves user information from the database and converts it into a format that Spring Security can understand.
 *
 * @see UserDetailsService
 * @see UserDetails
 */
@Service
@RequiredArgsConstructor
public class UsersDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final PgOwnerRepository pgOwnerRepository;


    /**
     * Loads the user details by username during authentication.
     *
     * @return a fully populated UserDetails object (never null)
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Try to load the user from PgOwnerEntity
        PgOwnerEntity pgOwner = pgOwnerRepository.findByOwnerContactNumber(username).orElse(null);
        if (pgOwner != null) {
            return new CustomUserDetails(pgOwner);
        }

        // 2. If not found, try to load the user from UserEntity
        UsersEntity user = userRepository.findByPhoneNumber(username).orElse(null);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        // 3. If neither is found, throw an exception
        throw new UsernameNotFoundException("User not found with email or phone: " + username);
    }
}
