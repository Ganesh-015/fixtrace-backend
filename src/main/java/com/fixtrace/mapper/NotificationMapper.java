package com.fixtrace.mapper;

import com.fixtrace.dto.notification.NotificationResponse;
import com.fixtrace.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }

        Long userId = notification.getUser() != null ? notification.getUser().getId() : null;

        return new NotificationResponse(
                notification.getId(),
                userId,
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
