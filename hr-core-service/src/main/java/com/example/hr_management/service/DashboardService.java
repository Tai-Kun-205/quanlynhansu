package com.example.hr_management.service;

import com.example.hr_management.dto.response.DashboardResponse;
import com.example.hr_management.entity.Attendance;
import com.example.hr_management.entity.Employee;
import com.example.hr_management.entity.LeaveRequest;
import com.example.hr_management.entity.Payroll;
import com.example.hr_management.repository.AttendanceRepository;
import com.example.hr_management.repository.DepartmentRepository;
import com.example.hr_management.repository.EmployeeRepository;
import com.example.hr_management.repository.LeaveRequestRepository;
import com.example.hr_management.repository.PositionRepository;
import com.example.hr_management.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.Map;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollRepository payrollRepository;

    public DashboardResponse getSummary() {
        DashboardResponse res = new DashboardResponse();
        res.setTotalEmployees(employeeRepository.count());
        res.setWorkingEmployees(employeeRepository.countByStatus(Employee.EmployeeStatus.WORKING));
        res.setOnLeaveEmployees(employeeRepository.countByStatus(Employee.EmployeeStatus.ON_LEAVE));
        res.setResignedEmployees(employeeRepository.countByStatus(Employee.EmployeeStatus.RESIGNED));
        res.setTotalDepartments(departmentRepository.count());
        res.setTotalPositions(positionRepository.count());
        res.setPendingLeaveRequests(leaveRequestRepository.countByStatus(LeaveRequest.LeaveStatus.PENDING));
        res.setTodayAttendanceCount(attendanceRepository.countByWorkDate(LocalDate.now()));
        res.setEmployeesByDepartment(employeeRepository.countGroupedByDepartment().stream()
                .map(row -> new DashboardResponse.DepartmentStatistic((String) row[1], (Long) row[2]))
                .collect(Collectors.toList()));
        res.setEmployeesByStatus(Arrays.asList(
                new DashboardResponse.StatusStatistic("Đang làm việc", res.getWorkingEmployees()),
                new DashboardResponse.StatusStatistic("Tạm nghỉ", res.getOnLeaveEmployees()),
                new DashboardResponse.StatusStatistic("Nghỉ việc", res.getResignedEmployees())));
        LocalDate today = LocalDate.now();
        long present = attendanceRepository.countByWorkDateAndStatus(today, Attendance.AttendanceStatus.PRESENT);
        long late = attendanceRepository.countByWorkDateAndStatus(today, Attendance.AttendanceStatus.LATE);
        long earlyLeave = attendanceRepository.countByWorkDateAndStatus(today, Attendance.AttendanceStatus.EARLY_LEAVE);
        long absent = attendanceRepository.countByWorkDateAndStatus(today, Attendance.AttendanceStatus.ABSENT);
        long checked = present + late + earlyLeave + absent;
        res.setTodayAttendanceByStatus(Arrays.asList(
                new DashboardResponse.StatusStatistic("Có mặt", present),
                new DashboardResponse.StatusStatistic("Đi muộn", late),
                new DashboardResponse.StatusStatistic("Về sớm", earlyLeave),
                new DashboardResponse.StatusStatistic("Nghỉ", absent),
                new DashboardResponse.StatusStatistic("Chưa chấm công", Math.max(0, employeeRepository.count() - checked))));
        List<Payroll> payrolls = payrollRepository.findAll();
        res.setPayrollCount(payrolls.size());
        res.setDraftPayrollCount(payrolls.stream().filter(p -> p.getStatus() == Payroll.PayrollStatus.DRAFT).count());
        res.setFinalizedPayrollCount(payrolls.stream().filter(p -> p.getStatus() == Payroll.PayrollStatus.FINALIZED).count());
        res.setTotalPayrollWorkDays(payrolls.stream().mapToLong(p -> p.getActualWorkDays() == null ? 0 : p.getActualWorkDays()).sum());
        BigDecimal totalPayroll = payrolls.stream().map(Payroll::getNetSalary).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        res.setTotalPayroll(totalPayroll);
        res.setAveragePayroll(payrolls.isEmpty() ? BigDecimal.ZERO :
                totalPayroll.divide(BigDecimal.valueOf(payrolls.size()), 2, java.math.RoundingMode.HALF_UP));
        Map<String, List<Payroll>> byDepartment = payrolls.stream().collect(Collectors.groupingBy(
                p -> p.getEmployee().getDepartment() == null ? "Chưa phân phòng ban" : p.getEmployee().getDepartment().getName(),
                LinkedHashMap::new, Collectors.toList()));
        res.setPayrollByDepartment(byDepartment.entrySet().stream()
                .map(entry -> new DashboardResponse.PayrollStatistic(entry.getKey(),
                        entry.getValue().stream().map(Payroll::getNetSalary).filter(java.util.Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add), entry.getValue().size()))
                .collect(Collectors.toList()));
        return res;
    }
}
