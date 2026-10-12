package com.example.hr_management.dto.request;

import lombok.Data;

@Data
public class ActivityLogRequest {
    private String action;
    private String module;
    private String targetType;
    private Long targetId;
    private String description;
    private String ipAddress;
}
