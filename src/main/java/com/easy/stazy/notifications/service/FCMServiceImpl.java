package com.easy.stazy.notifications.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FCMServiceImpl implements FCMService {
    private static final Logger logger = LoggerFactory.getLogger(FCMServiceImpl.class);

    public String sendNotificationToToken(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Sent message to token: {} with response: {}", token, response);
            return response;
        } catch (Exception e) {
            logger.error("Error sending FCM notification", e);
            return null;
        }
    }
}
