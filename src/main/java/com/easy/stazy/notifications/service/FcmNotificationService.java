package com.easy.stazy.notifications.service;
import com.easy.stazy.pgmanagement.pg.enums.UserType;

import java.util.Map;

public interface FcmNotificationService {
    void registerOrUpdateDeviceToken(Long identityId, UserType identityType, String fcmToken, String deviceType);
    void sendNotificationAndLog(Long identityId, UserType identityType, String title, String body, Map<String, String> dataPayload);
    void sendNotificationToUser(Long identityId, UserType identityType, String title, String body,Map<String,String> dataPayload);
    void sendNotificationToAllUsersOfType(UserType identityType, String title, String body);
    void sendSimpleNotification(Long identityId, UserType identityType, String title, String body);
}