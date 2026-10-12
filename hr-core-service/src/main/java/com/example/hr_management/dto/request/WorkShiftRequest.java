package com.example.hr_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class WorkShiftRequest {
    @NotBlank(message = "Mã ca không được để trống")
    private String code;
    @NotBlank(message = "Tên ca không được để trống")
    private String name;
    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime startTime;
    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime endTime;
    private String description;
    private Boolean active = true;
}
