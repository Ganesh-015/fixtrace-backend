package com.fixtrace.service;

import com.fixtrace.dto.notification.NotificationResponse;
import com.fixtrace.entity.User;
import com.fixtrace.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    void createNotification(User user, String title, String message, NotificationType type);

    List<NotificationResponse> getUserNotifications(String userEmail);

    List<NotificationResponse> getUnreadNotifications(String userEmail);

    void markAsRead(Long id, String userEmail);

    void markAllAsRead(String userEmail);
}
