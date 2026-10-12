package com.example.hr_management.controller;

import com.example.hr_management.dto.ApiResponse;
import com.example.hr_management.dto.request.WorkAssignmentRequest;
import com.example.hr_management.dto.response.WorkAssignmentResponse;
import com.example.hr_management.service.AttendanceService;
import com.example.hr_management.service.WorkAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-assignments")
@RequiredArgsConstructor
public class WorkAssignmentController {
    private final WorkAssignmentService service;
    private final AttendanceService attendanceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<WorkAssignmentResponse>>> search(
            @RequestParam(required = false) Long employeeId, @RequestParam(required = false) Long shiftId,
            @RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(service.search(employeeId, shiftId, fromDate, toDate, page, size)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<WorkAssignmentResponse>>> my(
            @RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long employeeId = attendanceService.getEmployeeIdByUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(service.search(employeeId, null, fromDate, toDate, page, size)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkAssignmentResponse>> create(@Valid @RequestBody WorkAssignmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Phân ca thành công", service.save(null, request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkAssignmentResponse>> update(@PathVariable Long id, @Valid @RequestBody WorkAssignmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật lịch làm việc thành công", service.save(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa lịch làm việc", null));
    }
}
