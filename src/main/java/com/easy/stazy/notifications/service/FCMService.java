package com.easy.stazy.notifications.service;

public interface FCMService {
    String sendNotificationToToken(String token, String title, String body);
}

