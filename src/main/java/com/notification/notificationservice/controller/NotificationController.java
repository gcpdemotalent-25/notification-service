package com.notification.notificationservice.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @PostMapping
    public void sendNotification(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        System.out.println("=========================================");
        System.out.println("ENVOI DE NOTIFICATION :");
        System.out.println(message);
        System.out.println("=========================================");
    }
}
