package com.example.hr_management.repository;

import com.example.hr_management.entity.NotificationRead;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {
    Optional<NotificationRead> findByNotificationIdAndUserId(Long notificationId, Long userId);
    long countByUserIdAndNotificationActiveTrueAndNotificationIdNotIn(Long userId, java.util.Collection<Long> ids);
}
