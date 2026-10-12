package com.example.hr_management.config;

import com.example.hr_management.entity.*;
import com.example.hr_management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bổ sung dữ liệu mẫu cho các chức năng ngoài nhân viên: chấm công, nghỉ phép, ca làm,
 * phân công, bảng lương, thông báo, nhật ký. Mỗi bảng chỉ được nạp khi còn trống,
 * nên chạy lại nhiều lần không bị nhân đôi dữ liệu.
 */
@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class SampleDataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final WorkShiftRepository workShiftRepository;
    private final WorkAssignmentRepository workAssignmentRepository;
    private final PayrollRepository payrollRepository;
    private final NotificationRepository notificationRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<Department> departments = departmentRepository.findAll();
        List<Position> positions = positionRepository.findAll();
        if (departments.isEmpty() || positions.isEmpty()) return;

        seedMoreEmployees(departments, positions);
        List<Employee> employees = employeeRepository.findAll();

        List<WorkShift> shifts = seedShifts();
        seedAssignments(employees, shifts);
        seedAttendance(employees);
        seedLeaveRequests(employees);
        seedPayrolls(employees);
        seedNotifications(departments);
        seedActivityLogs();
    }

    private void seedMoreEmployees(List<Department> departments, List<Position> positions) {
        if (employeeRepository.count() >= 6) return;
        Department d1 = departments.get(0);
        Department d2 = departments.get(departments.size() > 1 ? 1 : 0);
        Position p1 = positions.get(positions.size() > 1 ? 1 : 0);
        Position p2 = positions.get(positions.size() > 2 ? 2 : 0);
        addEmployee("NV004", "Phạm Thị Dung", "dung.pham@company.com", "0934567890",
                Employee.Gender.FEMALE, LocalDate.of(1995, 3, 8), LocalDate.of(2023, 2, 1), d1, p1, "nv004");
        addEmployee("NV005", "Hoàng Văn Em", "em.hoang@company.com", "0945678901",
                Employee.Gender.MALE, LocalDate.of(1993, 7, 22), LocalDate.of(2023, 9, 15), d1, p1, "nv005");
        addEmployee("NV006", "Vũ Thị Phương", "phuong.vu@company.com", "0956789012",
                Employee.Gender.FEMALE, LocalDate.of(1996, 12, 2), LocalDate.of(2024, 1, 10), d2, p2, "nv006");
        log.info("Da them nhan vien mau NV004-NV006");
    }

    private void addEmployee(String code, String name, String email, String phone, Employee.Gender gender,
                             LocalDate dob, LocalDate start, Department dept, Position pos, String username) {
        boolean exists = employeeRepository.findAll().stream().anyMatch(e -> code.equals(e.getCode()));
        if (exists || userRepository.existsByUsername(username)) return;
        Employee emp = new Employee();
        emp.setCode(code);
        emp.setFullName(name);
        emp.setEmail(email);
        emp.setPhone(phone);
        emp.setGender(gender);
        emp.setDateOfBirth(dob);
        emp.setStartDate(start);
        emp.setDepartment(dept);
        emp.setPosition(pos);
        emp.setStatus(Employee.EmployeeStatus.WORKING);
        employeeRepository.save(emp);

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setEmail(email);
        user.setRole(User.Role.EMPLOYEE);
        user.setStatus(User.AccountStatus.ACTIVE);
        user.setEmployee(emp);
        userRepository.save(user);
    }

    private List<WorkShift> seedShifts() {
        if (workShiftRepository.count() > 0) return workShiftRepository.findAll();
        List<WorkShift> list = new ArrayList<>();
        list.add(shift("CA01", "Ca hành chính", LocalTime.of(8, 0), LocalTime.of(17, 0), "Giờ hành chính 8h-17h"));
        list.add(shift("CA02", "Ca sáng", LocalTime.of(6, 0), LocalTime.of(14, 0), "Ca sáng 6h-14h"));
        list.add(shift("CA03", "Ca chiều", LocalTime.of(14, 0), LocalTime.of(22, 0), "Ca chiều 14h-22h"));
        log.info("Da them ca lam viec mau");
        return list;
    }

    private WorkShift shift(String code, String name, LocalTime start, LocalTime end, String desc) {
        WorkShift s = new WorkShift();
        s.setCode(code);
        s.setName(name);
        s.setStartTime(start);
        s.setEndTime(end);
        s.setDescription(desc);
        s.setActive(true);
        return workShiftRepository.save(s);
    }

    private void seedAssignments(List<Employee> employees, List<WorkShift> shifts) {
        if (workAssignmentRepository.count() > 0 || shifts.isEmpty() || employees.isEmpty()) return;
        LocalDate today = LocalDate.now();
        for (int day = 0; day < 7; day++) {
            LocalDate date = today.plusDays(day);
            if (isWeekend(date)) continue;
            for (int i = 0; i < employees.size(); i++) {
                WorkAssignment a = new WorkAssignment();
                a.setEmployee(employees.get(i));
                a.setShift(shifts.get((i + day) % shifts.size()));
                a.setWorkDate(date);
                a.setNote("Phân công mẫu");
                workAssignmentRepository.save(a);
            }
        }
        log.info("Da them phan cong mau");
    }

    private void seedAttendance(List<Employee> employees) {
        if (attendanceRepository.count() > 0) return;
        LocalDate day = LocalDate.now().minusDays(1);
        int made = 0;
        while (made < 14) {
            if (!isWeekend(day)) {
                for (int i = 0; i < employees.size(); i++) {
                    int pattern = (made + i) % 7;
                    Attendance a = new Attendance();
                    a.setEmployee(employees.get(i));
                    a.setWorkDate(day);
                    if (pattern == 5) {
                        a.setStatus(Attendance.AttendanceStatus.ABSENT);
                        a.setWorkHours(0.0);
                    } else if (pattern == 3) {
                        a.setCheckInTime(LocalTime.of(8, 40));
                        a.setCheckOutTime(LocalTime.of(17, 5));
                        a.setWorkHours(7.5);
                        a.setStatus(Attendance.AttendanceStatus.LATE);
                    } else if (pattern == 6) {
                        a.setCheckInTime(LocalTime.of(7, 55));
                        a.setCheckOutTime(LocalTime.of(15, 30));
                        a.setWorkHours(6.5);
                        a.setStatus(Attendance.AttendanceStatus.EARLY_LEAVE);
                    } else {
                        a.setCheckInTime(LocalTime.of(7, 50 + pattern));
                        a.setCheckOutTime(LocalTime.of(17, 10));
                        a.setWorkHours(8.0);
                        a.setStatus(Attendance.AttendanceStatus.PRESENT);
                    }
                    attendanceRepository.save(a);
                }
                made++;
            }
            day = day.minusDays(1);
        }
        log.info("Da them cham cong mau");
    }

    private void seedLeaveRequests(List<Employee> employees) {
        if (leaveRequestRepository.count() > 0 || employees.size() < 3) return;
        LocalDate today = LocalDate.now();
        leave(employees.get(0), today.plusDays(5), today.plusDays(6), "Việc gia đình", LeaveRequest.LeaveStatus.PENDING);
        leave(employees.get(1), today.minusDays(10), today.minusDays(9), "Khám sức khỏe", LeaveRequest.LeaveStatus.APPROVED);
        leave(employees.get(2), today.minusDays(20), today.minusDays(20), "Đi du lịch", LeaveRequest.LeaveStatus.REJECTED);
        if (employees.size() > 3) {
            leave(employees.get(3), today.plusDays(12), today.plusDays(14), "Nghỉ phép năm", LeaveRequest.LeaveStatus.PENDING);
        }
        log.info("Da them don nghi phep mau");
    }

    private void leave(Employee e, LocalDate from, LocalDate to, String reason, LeaveRequest.LeaveStatus status) {
        LeaveRequest r = new LeaveRequest();
        r.setEmployee(e);
        r.setStartDate(from);
        r.setEndDate(to);
        r.setReason(reason);
        r.setStatus(status);
        r.setCreatedAt(LocalDateTime.now().minusDays(2));
        leaveRequestRepository.save(r);
    }

    private void seedPayrolls(List<Employee> employees) {
        if (payrollRepository.count() > 0) return;
        LocalDate prev = LocalDate.now().minusMonths(1);
        LocalDate cur = LocalDate.now();
        for (int i = 0; i < employees.size(); i++) {
            long base = 10_000_000L + i * 2_000_000L;
            payroll(employees.get(i), prev.getMonthValue(), prev.getYear(), base, 26, 0, i % 2, Payroll.PayrollStatus.FINALIZED);
            payroll(employees.get(i), cur.getMonthValue(), cur.getYear(), base, 14, 1, 0, Payroll.PayrollStatus.DRAFT);
        }
        log.info("Da them bang luong mau");
    }

    private void payroll(Employee e, int month, int year, long base, int actualDays, int lateDays,
                         int absentDays, Payroll.PayrollStatus status) {
        Payroll p = new Payroll();
        p.setEmployee(e);
        p.setMonth(month);
        p.setYear(year);
        p.setBaseSalary(BigDecimal.valueOf(base));
        p.setStandardWorkDays(26);
        p.setActualWorkDays(actualDays);
        p.setAllowance(BigDecimal.valueOf(500_000));
        p.setBonus(status == Payroll.PayrollStatus.FINALIZED ? BigDecimal.valueOf(1_000_000) : BigDecimal.ZERO);
        p.setDeduction(BigDecimal.valueOf(200_000L * lateDays));
        p.setLateDays(lateDays);
        p.setAbsentDays(absentDays);
        p.setEarlyLeaveDays(0);
        BigDecimal attendanceSalary = BigDecimal.valueOf(base)
                .multiply(BigDecimal.valueOf(actualDays))
                .divide(BigDecimal.valueOf(26), 2, RoundingMode.HALF_UP);
        p.setAttendanceSalary(attendanceSalary);
        p.setNetSalary(attendanceSalary.add(p.getAllowance()).add(p.getBonus()).subtract(p.getDeduction()));
        p.setStatus(status);
        payrollRepository.save(p);
    }

    private void seedNotifications(List<Department> departments) {
        if (notificationRepository.count() > 0) return;
        notification("Chào mừng đến hệ thống", "Hệ thống quản lý nhân sự đã sẵn sàng sử dụng.", null);
        notification("Lịch nghỉ lễ", "Công ty nghỉ lễ theo quy định, vui lòng sắp xếp công việc trước kỳ nghỉ.", null);
        notification("Họp phòng ban", "Phòng sẽ họp đầu tuần vào lúc 9h tại phòng họp tầng 2.", departments.get(0));
        log.info("Da them thong bao mau");
    }

    private void notification(String title, String content, Department dept) {
        Notification n = new Notification();
        n.setTitle(title);
        n.setContent(content);
        n.setCreatedBy("admin");
        n.setDepartment(dept);
        n.setCreatedAt(LocalDateTime.now().minusDays(1));
        n.setActive(true);
        notificationRepository.save(n);
    }

    private void seedActivityLogs() {
        if (activityLogRepository.count() > 0) return;
        log("LOGIN", "AUTH", "User", 1L, "Admin đăng nhập hệ thống", 5);
        log("CREATE", "EMPLOYEE", "Employee", 4L, "Thêm nhân viên NV004", 4);
        log("APPROVE", "LEAVE_REQUEST", "LeaveRequest", 2L, "Duyệt đơn nghỉ phép", 3);
        log("UPDATE", "PAYROLL", "Payroll", 1L, "Chốt bảng lương tháng trước", 2);
        log.info("Da them nhat ky hoat dong mau");
    }

    private void log(String action, String module, String targetType, Long targetId, String desc, int daysAgo) {
        ActivityLog l = new ActivityLog();
        l.setActorUsername("admin");
        l.setAction(action);
        l.setModule(module);
        l.setTargetType(targetType);
        l.setTargetId(targetId);
        l.setDescription(desc);
        l.setIpAddress("127.0.0.1");
        l.setCreatedAt(LocalDateTime.now().minusDays(daysAgo));
        activityLogRepository.save(l);
    }

    private boolean isWeekend(LocalDate d) {
        return d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}

