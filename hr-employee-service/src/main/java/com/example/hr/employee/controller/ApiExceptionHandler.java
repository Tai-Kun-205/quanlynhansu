package com.example.hr.employee.controller;

import com.example.hr.employee.dto.ApiResponse;
import com.example.hr.employee.service.EmployeeQueryService.EmployeeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(EmployeeNotFoundException.class)
    public org.springframework.http.ResponseEntity<ApiResponse<Void>> notFound(EmployeeNotFoundException ex) {
        return org.springframework.http.ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public org.springframework.http.ResponseEntity<ApiResponse<Void>> badRequest(IllegalArgumentException ex) {
        return org.springframework.http.ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Tham số không hợp lệ", null));
    }
}
