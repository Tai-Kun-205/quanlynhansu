package com.example.hr_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PositionRequest {
    @NotBlank(message = "Mã chức vụ không được để trống")
    private String code;

    @NotBlank(message = "Tên chức vụ không được để trống")
    private String name;

    private String description;
    private String status;
}

