package com.example.hr_management.controller;

import com.example.hr_management.dto.ApiResponse;
import com.example.hr_management.dto.response.AttendanceResponse;
import com.example.hr_management.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAll(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.getAll(employeeId, startDate, endDate, status, page, size)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getMyAttendance(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long employeeId = attendanceService.getEmployeeIdByUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.getAll(employeeId, startDate, endDate, null, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getById(id)));
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(Authentication authentication) {
        Long employeeId = attendanceService.getEmployeeIdByUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Chấm công vào thành công", attendanceService.checkIn(employeeId)));
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(Authentication authentication) {
        Long employeeId = attendanceService.getEmployeeIdByUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Chấm công ra thành công", attendanceService.checkOut(employeeId)));
    }
}

