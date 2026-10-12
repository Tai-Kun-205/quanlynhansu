package com.example.hr.employee.controller;

import com.example.hr.employee.dto.ApiResponse;
import com.example.hr.employee.dto.EmployeeResponse;
import com.example.hr.employee.service.EmployeeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/hr/employees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EmployeeQueryController {
    private final EmployeeQueryService service;

    @GetMapping
    public ApiResponse<Page<EmployeeResponse>> search(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Long departmentId,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(service.search(keyword, departmentId, status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }
}
