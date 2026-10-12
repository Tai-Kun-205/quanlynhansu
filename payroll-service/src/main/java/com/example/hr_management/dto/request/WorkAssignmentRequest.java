package com.example.hr_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkAssignmentRequest {
    @NotNull(message = "Nhân viên không được để trống")
    private Long employeeId;
    @NotNull(message = "Ca làm việc không được để trống")
    private Long shiftId;
    @NotNull(message = "Ngày làm việc không được để trống")
    private LocalDate workDate;
    private String note;
}
