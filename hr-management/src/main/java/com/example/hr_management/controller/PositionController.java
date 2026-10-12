package com.example.hr_management.controller;

import com.example.hr_management.dto.ApiResponse;
import com.example.hr_management.dto.request.PositionRequest;
import com.example.hr_management.dto.response.PositionResponse;
import com.example.hr_management.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PositionResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        List<PositionResponse> list;
        if (keyword != null && !keyword.isBlank()) {
            list = positionService.search(keyword);
        } else if (status != null && !status.isBlank()) {
            list = positionService.getByStatus(status);
        } else {
            list = positionService.getAll();
        }
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PositionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(positionService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PositionResponse>> create(@Valid @RequestBody PositionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm chức vụ thành công", positionService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PositionResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody PositionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật chức vụ thành công", positionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        positionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa chức vụ thành công", null));
    }
}

