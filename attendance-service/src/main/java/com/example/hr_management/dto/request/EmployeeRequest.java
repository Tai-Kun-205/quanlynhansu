package com.example.hr_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRequest {
    @NotBlank(message = "Mã nhân viên không được để trống")
    private String code;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    private LocalDate dateOfBirth;
    private String gender;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private String address;

    private String avatarUrl;
    private String emergencyContactName;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "Số điện thoại người liên hệ không hợp lệ")
    private String emergencyContactPhone;

    private String emergencyContactRelationship;
    private String educationLevel;
    private String major;

    @NotNull(message = "Ngày vào làm không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Phòng ban không được để trống")
    private Long departmentId;

    @NotNull(message = "Chức vụ không được để trống")
    private Long positionId;

    private String status;

    // For creating user account
    private String username;
    private String password;
}
