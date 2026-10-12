package com.example.hr_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "payrolls", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id","payroll_month","payroll_year"}))
@Data @NoArgsConstructor
public class Payroll {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Chỉ lưu khóa nhân viên (cột employee_id giữ nguyên); thông tin nhân viên lấy từ hr-core-service qua SOAP. */
    @Column(name = "employee_id", nullable = false) private Long employeeId;
    @Column(name="payroll_month", nullable=false) private Integer month;
    @Column(name="payroll_year", nullable=false) private Integer year;
    @Column(name="base_salary", nullable=false, precision=15, scale=2) private BigDecimal baseSalary = BigDecimal.ZERO;
    @Column(name="standard_work_days", nullable=false) private Integer standardWorkDays = 26;
    @Column(name="actual_work_days", nullable=false) private Integer actualWorkDays = 0;
    @Column(precision=15, scale=2) private BigDecimal allowance = BigDecimal.ZERO;
    @Column(precision=15, scale=2) private BigDecimal bonus = BigDecimal.ZERO;
    @Column(precision=15, scale=2) private BigDecimal deduction = BigDecimal.ZERO;
    @Column(name="net_salary", nullable=false, precision=15, scale=2) private BigDecimal netSalary = BigDecimal.ZERO;
    @Column(name="attendance_salary", nullable=false, precision=15, scale=2) private BigDecimal attendanceSalary = BigDecimal.ZERO;
    @Column(name="late_days", nullable=false) private Integer lateDays = 0;
    @Column(name="early_leave_days", nullable=false) private Integer earlyLeaveDays = 0;
    @Column(name="absent_days", nullable=false) private Integer absentDays = 0;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private PayrollStatus status = PayrollStatus.DRAFT;
    public enum PayrollStatus { DRAFT, FINALIZED }
}
