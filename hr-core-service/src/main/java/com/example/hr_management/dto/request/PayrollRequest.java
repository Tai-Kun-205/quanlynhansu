package com.example.hr_management.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
@Data public class PayrollRequest {
    @NotNull private Long employeeId; @Min(1) @Max(12) private Integer month; @Min(2000) private Integer year;
    @NotNull @DecimalMin("0") private BigDecimal baseSalary; @Min(1) private Integer standardWorkDays=26;
    @Min(0) private Integer actualWorkDays=0; @DecimalMin("0") private BigDecimal allowance=BigDecimal.ZERO;
    @DecimalMin("0") private BigDecimal bonus=BigDecimal.ZERO; @DecimalMin("0") private BigDecimal deduction=BigDecimal.ZERO;
}
