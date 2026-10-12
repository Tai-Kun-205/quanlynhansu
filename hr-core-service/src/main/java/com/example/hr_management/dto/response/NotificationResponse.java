package com.example.hr_management.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id; private String title; private String content; private String createdBy;
    private String departmentName; private LocalDateTime createdAt; private boolean read;
}
