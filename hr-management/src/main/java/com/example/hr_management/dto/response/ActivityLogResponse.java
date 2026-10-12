package com.example.hr_management.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityLogResponse {
    private Long id; private String actorUsername; private String action; private String module;
    private String targetType; private Long targetId; private String description;
    private LocalDateTime createdAt; private String ipAddress;
}
