package com.example.hr_management.controller;

import com.example.hr_management.dto.ApiResponse;
import com.example.hr_management.dto.response.UserResponse;
import com.example.hr_management.service.UserService;
import com.example.hr_management.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;
    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAll(keyword, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id)));
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<UserResponse>> lock(@PathVariable Long id, org.springframework.security.core.Authentication auth) {
        UserResponse response = userService.lockAccount(id);
        activityLogService.record(auth.getName(), "LOCK", "TÀI KHOẢN", "User", id, "Khóa tài khoản " + response.getUsername(), null);
        return ResponseEntity.ok(ApiResponse.success("Tài khoản đã được khóa", response));
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<UserResponse>> unlock(@PathVariable Long id, org.springframework.security.core.Authentication auth) {
        UserResponse response = userService.unlockAccount(id);
        activityLogService.record(auth.getName(), "UNLOCK", "TÀI KHOẢN", "User", id, "Mở khóa tài khoản " + response.getUsername(), null);
        return ResponseEntity.ok(ApiResponse.success("Tài khoản đã được mở khóa", response));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> changeRole(@PathVariable Long id,
                                                                 @RequestParam String role,
                                                                 org.springframework.security.core.Authentication auth) {
        UserResponse response = userService.changeRole(id, role);
        activityLogService.record(auth.getName(), "CHANGE_ROLE", "TÀI KHOẢN", "User", id, "Thay đổi vai trò tài khoản " + response.getUsername(), null);
        return ResponseEntity.ok(ApiResponse.success("Vai trò đã được thay đổi", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, org.springframework.security.core.Authentication auth) {
        userService.delete(id);
        activityLogService.record(auth.getName(), "DELETE", "TÀI KHOẢN", "User", id, "Xóa tài khoản", null);
        return ResponseEntity.ok(ApiResponse.success("Xóa tài khoản thành công", null));
    }
}
