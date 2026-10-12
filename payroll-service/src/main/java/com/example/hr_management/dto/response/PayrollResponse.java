package com.example.hr_management.dto.response;
import lombok.Data;
import java.math.BigDecimal;
@Data public class PayrollResponse {
    private Long id,employeeId; private String employeeCode,employeeName,departmentName,status;
    private Integer month,year,standardWorkDays,actualWorkDays,lateDays,earlyLeaveDays,absentDays; private BigDecimal baseSalary,attendanceSalary,allowance,bonus,deduction,netSalary;
}
