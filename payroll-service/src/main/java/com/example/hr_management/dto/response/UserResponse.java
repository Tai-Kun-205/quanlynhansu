package com.example.hr_management.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String status;
    private Long employeeId;
    private String employeeName;
}

