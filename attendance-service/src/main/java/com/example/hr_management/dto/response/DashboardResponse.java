package com.example.hr_management.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardResponse {
    private long totalEmployees;
    private long workingEmployees;
    private long onLeaveEmployees;
    private long resignedEmployees;
    private long totalDepartments;
    private long totalPositions;
    private long pendingLeaveRequests;
    private long todayAttendanceCount;
    private java.util.List<DepartmentStatistic> employeesByDepartment;
    private java.util.List<StatusStatistic> employeesByStatus;
    private java.util.List<StatusStatistic> todayAttendanceByStatus;
    private BigDecimal totalPayroll = BigDecimal.ZERO;
    private long payrollCount;
    private long draftPayrollCount;
    private long finalizedPayrollCount;
    private long totalPayrollWorkDays;
    private BigDecimal averagePayroll = BigDecimal.ZERO;
    private List<PayrollStatistic> payrollByDepartment;

    @Data
    public static class DepartmentStatistic {
        private String name;
        private long count;

        public DepartmentStatistic(String name, long count) {
            this.name = name;
            this.count = count;
        }
    }

    @Data
    public static class StatusStatistic {
        private String label;
        private long count;

        public StatusStatistic(String label, long count) {
            this.label = label;
            this.count = count;
        }

    }

    @Data
    public static class PayrollStatistic {
        private String label;
        private BigDecimal total = BigDecimal.ZERO;
        private long count;

        public PayrollStatistic(String label, BigDecimal total, long count) {
            this.label = label;
            this.total = total;
            this.count = count;
        }
    }
}
