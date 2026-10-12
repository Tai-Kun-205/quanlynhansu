package com.example.hr_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
public class ActivityLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "actor_username", nullable = false, length = 100) private String actorUsername;
    @Column(nullable = false, length = 50) private String action;
    @Column(nullable = false, length = 50) private String module;
    @Column(name = "target_type", length = 100) private String targetType;
    @Column(name = "target_id") private Long targetId;
    @Column(length = 500) private String description;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "ip_address", length = 45) private String ipAddress;
}
