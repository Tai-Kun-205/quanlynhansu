package com.example.hr_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequest {
    @NotBlank(message = "Mã phòng ban không được để trống")
    private String code;

    @NotBlank(message = "Tên phòng ban không được để trống")
    private String name;

    private String description;
    private String status;
}

