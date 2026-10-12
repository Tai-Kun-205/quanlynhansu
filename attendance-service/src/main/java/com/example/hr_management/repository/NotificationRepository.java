package com.example.hr_management.repository;

import com.example.hr_management.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByActiveTrueOrderByCreatedAtDesc();
}
