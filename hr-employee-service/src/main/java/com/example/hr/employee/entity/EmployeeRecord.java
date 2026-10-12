package com.example.hr.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class EmployeeRecord {
    protected EmployeeRecord() {
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String code;
    @Column(name = "full_name", nullable = false) private String fullName;
    @Column(name = "date_of_birth") private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING) private Gender gender;
    private String phone;
    private String email;
    private String address;
    @Column(name = "avatar_url") private String avatarUrl;
    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "department_id") private Long departmentId;
    @Column(name = "position_id") private Long positionId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Status status;

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Gender getGender() { return gender; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getAvatarUrl() { return avatarUrl; }
    public LocalDate getStartDate() { return startDate; }
    public Long getDepartmentId() { return departmentId; }
    public Long getPositionId() { return positionId; }
    public Status getStatus() { return status; }
    public enum Gender { MALE, FEMALE, OTHER }
    public enum Status { WORKING, ON_LEAVE, RESIGNED }
}
