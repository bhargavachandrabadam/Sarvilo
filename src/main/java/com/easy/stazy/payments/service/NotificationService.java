package com.easy.stazy.payments.service;

public interface NotificationService {
    void sendPushNotification(Long userId, String message);
}