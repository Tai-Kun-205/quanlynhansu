package com.example.hr_management.controller;

import com.example.hr_management.dto.ApiResponse;
import com.example.hr_management.dto.request.NetworkStatusRequest;
import com.example.hr_management.dto.response.NetworkStatusResponse;
import com.example.hr_management.service.NetworkAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo/network-status")
@RequiredArgsConstructor
public class DemoNetworkController {

    private final NetworkAccessService networkAccessService;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NetworkStatusResponse>> getStatus() {
        return ResponseEntity.ok(ApiResponse.success(networkAccessService.getStatus()));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NetworkStatusResponse>> updateStatus(
            @Valid @RequestBody NetworkStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Đã cập nhật trạng thái mạng",
                networkAccessService.updateStatus(request.getCompanyNetwork())));
    }
}
