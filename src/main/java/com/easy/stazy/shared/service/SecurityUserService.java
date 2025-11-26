package com.easy.stazy.shared.service;


import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.pgmanagement.admin.repository.PgOwnerRepository;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class SecurityUserService {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUserService.class);
    private final UserRepository userRepository;
    private final PgOwnerRepository ownerRepository;


    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof com.easy.stazy.authentication.entities.UsersEntity user) {
                return user.getId().toString();
            } else if (principal instanceof com.easy.stazy.shared.filters.JwtAuthenticationFilter.CustomPrincipal customPrincipal) {
                return customPrincipal.id().toString();
            } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
                return userDetails.getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    public String getCurrentUserName() {
        String userId = getCurrentUserId();
        if (userId != null && !userId.isEmpty()) {
            try {
                Long userIdLong = Long.parseLong(userId);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getAuthorities() != null) {
                    boolean isOwner = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equalsIgnoreCase("OWNER"));
                    boolean isUser = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equalsIgnoreCase("USER"));
                    if (isOwner) {
                        PgOwnerEntity ownerEntity = ownerRepository.findById(userIdLong).orElse(null);
                        if (ownerEntity != null && ownerEntity.getOwnerName() != null) {
                            return ownerEntity.getOwnerName().trim();
                        } else if (ownerEntity != null) {
                            return null;
                        }
                    } else if (isUser) {
                        UsersEntity userEntity = userRepository.findById(userIdLong).orElse(null);
                        if (userEntity != null && userEntity.getName() != null) {
                            return userEntity.getName().trim();
                        } else if (userEntity != null) {
                            return null;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                logger.error("Failed to parse userId '{}' to Long", userId, e);
                return null;
            }
        }
        return null;
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase(role) || a.getAuthority().equalsIgnoreCase(role));
        }
        return false;
    }

    public String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            return authentication.getAuthorities().iterator().next().getAuthority();
        }
        return null;
    }

}
