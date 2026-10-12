package com.example.hr_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {
    @NotBlank(message = "Tiêu đề không được để trống") private String title;
    @NotBlank(message = "Nội dung không được để trống") private String content;
    private Long departmentId;
}
