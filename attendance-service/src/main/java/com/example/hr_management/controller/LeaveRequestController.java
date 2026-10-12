package com.example.hr_management.controller;

import com.example.hr_management.dto.ApiResponse;
import com.example.hr_management.dto.request.LeaveRequestDto;
import com.example.hr_management.dto.response.LeaveRequestResponse;
import com.example.hr_management.service.LeaveRequestService;
import com.example.hr_management.service.ActivityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LeaveRequestResponse>>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        // Admin sees all, employee sees only their own
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Long employeeId = isAdmin ? null : leaveRequestService.getEmployeeIdByUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(
                leaveRequestService.getAll(employeeId, status, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(leaveRequestService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> create(
            @Valid @RequestBody LeaveRequestDto dto,
            Authentication authentication) {
        Long employeeId = leaveRequestService.getEmployeeIdByUsername(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đơn nghỉ phép đã được gửi", leaveRequestService.create(employeeId, dto)));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> approve(@PathVariable Long id, Authentication authentication) {
        LeaveRequestResponse response = leaveRequestService.approve(id);
        activityLogService.record(authentication.getName(), "APPROVE", "NGHỈ PHÉP", "LeaveRequest", id, "Duyệt đơn nghỉ phép", null);
        return ResponseEntity.ok(ApiResponse.success("Đơn nghỉ phép đã được duyệt", response));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> reject(@PathVariable Long id, Authentication authentication) {
        LeaveRequestResponse response = leaveRequestService.reject(id);
        activityLogService.record(authentication.getName(), "REJECT", "NGHỈ PHÉP", "LeaveRequest", id, "Từ chối đơn nghỉ phép", null);
        return ResponseEntity.ok(ApiResponse.success("Đơn nghỉ phép đã bị từ chối", response));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> cancel(@PathVariable Long id,
                                                                     Authentication authentication) {
        Long employeeId = leaveRequestService.getEmployeeIdByUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Đơn nghỉ phép đã được hủy", leaveRequestService.cancel(id, employeeId)));
    }
}
