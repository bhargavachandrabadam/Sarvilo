package com.easy.stazy.notifications.repository;

import com.easy.stazy.notifications.entity.FcmNotificationLog;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcmNotificationLogRepository extends JpaRepository<FcmNotificationLog, Long> {
    List<FcmNotificationLog> findByIdentityIdAndIdentityType(Long identityId, UserType identityType);
    List<FcmNotificationLog> findByStatus(String status);
}
