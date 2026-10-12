package com.example.hr.employee.dto;

import java.time.LocalDate;

public record EmployeeResponse(
        Long id, String code, String fullName, LocalDate dateOfBirth, String gender,
        String phone, String email, String address, String avatarUrl,
        LocalDate startDate, Long departmentId, Long positionId, String status) {
}
