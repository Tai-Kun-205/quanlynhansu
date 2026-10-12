package com.example.hr_management.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeProfileRequest {
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String phone;
    private String address;
    private String avatarUrl;
    private String emergencyContactName;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "Số điện thoại người liên hệ không hợp lệ")
    private String emergencyContactPhone;

    private String emergencyContactRelationship;
    private String educationLevel;
    private String major;
}
