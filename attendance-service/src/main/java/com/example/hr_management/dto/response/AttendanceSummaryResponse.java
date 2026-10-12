package com.example.hr_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Hop dong REST noi bo: tong hop cham cong cua 1 nhan vien trong 1 ky luong (thang/nam). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryResponse {
    private Long employeeId;
    private Integer month;
    private Integer year;
    /** So ngay co mat: PRESENT + LATE + EARLY_LEAVE */
    private Integer actualDays;
    private Integer lateDays;
    private Integer earlyLeaveDays;
    private Integer absentDays;
}