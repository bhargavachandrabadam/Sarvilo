package com.easy.stazy.notifications.repository;

import com.easy.stazy.notifications.entity.FcmDeviceToken;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmDeviceTokenRepository extends JpaRepository<FcmDeviceToken, Long> {
    Optional<FcmDeviceToken> findByFcmToken(String fcmToken);

    List<FcmDeviceToken> findByIdentityIdAndIdentityType(Long identityId, UserType identityType);

    List<FcmDeviceToken> findByIdentityType(UserType identityType);
}
