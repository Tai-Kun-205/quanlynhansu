package com.example.hr_management.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeResponse {
    private Long id;
    private String code;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String avatarUrl;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private String educationLevel;
    private String major;
    private LocalDate startDate;
    private Integer seniorityYears;
    private Long departmentId;
    private String departmentName;
    private Long positionId;
    private String positionName;
    private String status;
    private Long userId;
    private String username;
}
