package com.easy.stazy.notifications.controller;

import com.easy.stazy.pgmanagement.pg.enums.UserType;
import com.easy.stazy.notifications.service.FcmNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/fcm")
@RequiredArgsConstructor
public class FcmNotificationController {
    private final FcmNotificationService fcmNotificationService;

    @PostMapping("/register-token")
    public ResponseEntity<?> registerToken(@RequestParam Long identityId,
                                           @RequestParam UserType identityType,
                                           @RequestParam String fcmToken,
                                           @RequestParam String deviceType) {
        fcmNotificationService.registerOrUpdateDeviceToken(identityId, identityType, fcmToken, deviceType);
        return ResponseEntity.ok("Token registered/updated successfully");
    }

    @PostMapping("/test-send")
    public ResponseEntity<?> testSend(@RequestParam Long identityId,
                                      @RequestParam UserType identityType) {
        String title = "Stazy App";
        String body = "Thank you for using Stazy APP";
        fcmNotificationService.sendSimpleNotification(identityId, identityType, title, body);
        return ResponseEntity.ok("Test notification sent");
    }
}
