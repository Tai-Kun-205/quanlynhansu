package com.example.hr_management.dto.response;

import lombok.Data;
import java.time.LocalTime;

@Data
public class WorkShiftResponse {
    private Long id;
    private String code;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private boolean active;
}
