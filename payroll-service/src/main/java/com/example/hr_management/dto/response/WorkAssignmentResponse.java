package com.example.hr_management.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class WorkAssignmentResponse {
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private Long shiftId;
    private String shiftCode;
    private String shiftName;
    private String startTime;
    private String endTime;
    private LocalDate workDate;
    private String note;
}
