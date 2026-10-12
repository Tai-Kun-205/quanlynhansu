package com.example.hr_management.controller;

import com.example.hr_management.dto.ApiResponse;
import com.example.hr_management.dto.response.AttendanceSummaryResponse;
import com.example.hr_management.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API noi bo giua cac dich vu (khong di qua api-gateway). Yeu cau header X-Service-Key (vai tro SERVICE).
 * Vi du: GET /internal/attendances/summary?employeeId=5&month=10&year=2026
 */
@RestController
@RequestMapping("/internal/attendances")
@RequiredArgsConstructor
public class InternalAttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('SERVICE')")
    public ApiResponse<AttendanceSummaryResponse> summary(@RequestParam Long employeeId,
                                                          @RequestParam Integer month,
                                                          @RequestParam Integer year) {
        return ApiResponse.success(attendanceService.summarize(employeeId, month, year));
    }
}