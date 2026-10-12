package com.example.hr_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String role;
    private Long userId;
    private Long employeeId;

    public LoginResponse(String token, String username, String role, Long userId, Long employeeId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.userId = userId;
        this.employeeId = employeeId;
    }
}

