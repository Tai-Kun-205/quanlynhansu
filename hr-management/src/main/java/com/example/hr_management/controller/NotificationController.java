package com.example.hr_management.controller;

import com.example.hr_management.dto.ApiResponse;
import com.example.hr_management.dto.request.NotificationRequest;
import com.example.hr_management.dto.response.NotificationResponse;
import com.example.hr_management.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/notifications") @RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;
    @GetMapping("/my") @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> my(Authentication a) {
        return ResponseEntity.ok(ApiResponse.success(service.forUser(a.getName())));
    }
    @GetMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> all() {
        return ResponseEntity.ok(ApiResponse.success(service.all()));
    }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> create(@Valid @RequestBody NotificationRequest r, Authentication a) {
        return ResponseEntity.ok(ApiResponse.success("Tạo thông báo thành công", service.create(r, a.getName())));
    }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> update(@PathVariable Long id, @Valid @RequestBody NotificationRequest r) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông báo thành công", service.update(id, r)));
    }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id); return ResponseEntity.ok(ApiResponse.success("Đã xóa thông báo", null));
    }
    @PutMapping("/{id}/read") @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> read(@PathVariable Long id, Authentication a) {
        service.markRead(id, a.getName()); return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu đã đọc", null));
    }

    @GetMapping("/unread-count") @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> unreadCount(Authentication a) {
        return ResponseEntity.ok(ApiResponse.success(service.unreadCount(a.getName())));
    }

    @PutMapping("/read-all") @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> readAll(Authentication a) {
        service.markAllRead(a.getName());
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu tất cả thông báo đã đọc", null));
    }
}
