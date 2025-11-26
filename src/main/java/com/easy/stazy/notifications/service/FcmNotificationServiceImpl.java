package com.easy.stazy.notifications.service;

import com.easy.stazy.notifications.entity.FcmDeviceToken;
import com.easy.stazy.notifications.entity.FcmNotificationLog;
import com.easy.stazy.notifications.repository.FcmDeviceTokenRepository;
import com.easy.stazy.notifications.repository.FcmNotificationLogRepository;
import com.easy.stazy.pgmanagement.pg.enums.UserType;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FcmNotificationServiceImpl implements FcmNotificationService {
    private final FcmDeviceTokenRepository deviceTokenRepository;

    private final FcmNotificationLogRepository notificationLogRepository;

    @Override
    @Transactional
    public void registerOrUpdateDeviceToken(Long identityId, UserType identityType, String fcmToken, String deviceType) {
        Optional<FcmDeviceToken> existing = deviceTokenRepository.findByFcmToken(fcmToken);
        FcmDeviceToken token = existing.orElseGet(FcmDeviceToken::new);
        token.setIdentityId(identityId);
        token.setIdentityType(identityType);
        token.setFcmToken(fcmToken);
        token.setDeviceType(deviceType);
        token.setLastUpdated(LocalDateTime.now());
        deviceTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void sendNotificationAndLog(Long identityId, UserType identityType, String title, String body, Map<String, String> dataPayload) {
        // Find device tokens for the user
        var tokens = deviceTokenRepository.findByIdentityIdAndIdentityType(identityId, identityType);
        List<String> tokenStrings = tokens.stream().map(FcmDeviceToken::getFcmToken).collect(Collectors.toList());
        if (tokenStrings.isEmpty()) return;
        String status = "failed";
        String responseId = null;
        try {
            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .addAllTokens(tokenStrings);
            if (dataPayload != null) {
                messageBuilder.putAllData(dataPayload);
            }
            MulticastMessage message = messageBuilder.build();
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            status = response.getFailureCount() == 0 ? "sent" : "partial_failure";
            responseId = "Success: " + response.getSuccessCount() + ", Failure: " + response.getFailureCount();
        } catch (Exception e) {
            status = "failed";
            responseId = e.getMessage();
        }
        // Log notification for each token
        for (String token : tokenStrings) {
            FcmNotificationLog log = new FcmNotificationLog();
            log.setIdentityId(identityId);
            log.setIdentityType(identityType);
            log.setFcmToken(token);
            log.setTitle(title);
            log.setBody(body);
            log.setDataPayload(dataPayload != null ? dataPayload.toString() : null);
            log.setStatus(status);
            log.setResponseId(responseId);
            log.setCreatedAt(LocalDateTime.now());
            notificationLogRepository.save(log);
        }
    }

    // Overloaded for backward compatibility
    @Override
    public void sendNotificationToUser(Long identityId, UserType identityType, String title, String body, Map<String, String> dataPayload) {
        sendNotificationAndLog(identityId, identityType, title, body, dataPayload);
    }

    /**
     * @param identityType
     * @param title
     * @param body
     */
    @Override
    public void sendNotificationToAllUsersOfType(UserType identityType, String title, String body) {

    }

    @Override
    public void sendSimpleNotification(Long identityId, UserType identityType, String title, String body) {
        sendNotificationAndLog(identityId, identityType, title, body, null);
    }

}
