package com.example.hr_management.service;

import com.example.hr_management.entity.Attendance;
import com.example.hr_management.entity.Employee;
import com.example.hr_management.entity.LeaveRequest;
import com.example.hr_management.repository.AttendanceRepository;
import com.example.hr_management.repository.EmployeeRepository;
import com.example.hr_management.repository.LeaveRequestRepository;
import com.example.hr_management.repository.PayrollRepository;
import com.example.hr_management.entity.Payroll;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;

    @Transactional(readOnly = true)
    public byte[] exportEmployees(String keyword, Long departmentId, Long positionId, String status) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Nhân viên");
            writeRow(sheet, 0, "Mã nhân viên", "Họ tên", "Ngày sinh", "Giới tính", "Số điện thoại",
                    "Email", "Phòng ban", "Chức vụ", "Ngày vào làm", "Trạng thái");
            int rowIndex = 1;
            Stream<Employee> employees = employeeRepository.findAll().stream()
                    .filter(e -> keyword == null || keyword.isBlank()
                            || contains(e.getCode(), keyword) || contains(e.getFullName(), keyword)
                            || contains(e.getEmail(), keyword) || contains(e.getPhone(), keyword))
                    .filter(e -> departmentId == null || (e.getDepartment() != null && departmentId.equals(e.getDepartment().getId())))
                    .filter(e -> positionId == null || (e.getPosition() != null && positionId.equals(e.getPosition().getId())))
                    .filter(e -> status == null || status.isBlank() || (e.getStatus() != null && e.getStatus().name().equals(status)));
            for (Employee employee : employees.toList()) {
                writeRow(sheet, rowIndex++, employee.getCode(), employee.getFullName(), value(employee.getDateOfBirth()),
                        genderLabel(employee.getGender()), employee.getPhone(), employee.getEmail(),
                        employee.getDepartment() != null ? employee.getDepartment().getName() : "",
                        employee.getPosition() != null ? employee.getPosition().getName() : "",
                        value(employee.getStartDate()), employeeStatusLabel(employee.getStatus()));
            }
            return toBytes(workbook, sheet, 10);
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportAttendances(Long employeeId, Long departmentId, String startDate, String endDate, String status) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Chấm công");
            writeRow(sheet, 0, "Mã nhân viên", "Họ tên", "Phòng ban", "Ngày", "Giờ vào", "Giờ ra", "Số giờ làm", "Trạng thái");
            int rowIndex = 1;
            LocalDate from = parseDate(startDate);
            LocalDate to = parseDate(endDate);
            Stream<Attendance> attendances = attendanceRepository.findAll().stream()
                    .filter(a -> employeeId == null || employeeId.equals(a.getEmployee().getId()))
                    .filter(a -> departmentId == null || (a.getEmployee().getDepartment() != null && departmentId.equals(a.getEmployee().getDepartment().getId())))
                    .filter(a -> from == null || !a.getWorkDate().isBefore(from))
                    .filter(a -> to == null || !a.getWorkDate().isAfter(to))
                    .filter(a -> status == null || status.isBlank() || (a.getStatus() != null && a.getStatus().name().equals(status)));
            for (Attendance attendance : attendances.toList()) {
                writeRow(sheet, rowIndex++, attendance.getEmployee().getCode(), attendance.getEmployee().getFullName(),
                        attendance.getEmployee().getDepartment() == null ? "" : attendance.getEmployee().getDepartment().getName(),
                        value(attendance.getWorkDate()), value(attendance.getCheckInTime()), value(attendance.getCheckOutTime()),
                        attendance.getWorkHours() != null ? attendance.getWorkHours().toString() : "",
                        attendanceStatusLabel(attendance.getStatus()));
            }
            return toBytes(workbook, sheet, 8);
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportLeaveRequests(Long employeeId, Long departmentId, String status, Integer month, Integer year) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Nghỉ phép");
            writeRow(sheet, 0, "Mã đơn", "Mã nhân viên", "Họ tên", "Phòng ban", "Ngày bắt đầu", "Ngày kết thúc",
                    "Số ngày", "Lý do", "Ngày tạo", "Trạng thái");
            int rowIndex = 1;
            YearMonth period = month != null && year != null ? YearMonth.of(year, month) : null;
            Stream<LeaveRequest> requests = leaveRequestRepository.findAll().stream()
                    .filter(r -> employeeId == null || employeeId.equals(r.getEmployee().getId()))
                    .filter(r -> departmentId == null || (r.getEmployee().getDepartment() != null && departmentId.equals(r.getEmployee().getDepartment().getId())))
                    .filter(r -> status == null || status.isBlank() || (r.getStatus() != null && r.getStatus().name().equals(status)))
                    .filter(r -> period == null || overlaps(r.getStartDate(), r.getEndDate(), period));
            for (LeaveRequest request : requests.toList()) {
                writeRow(sheet, rowIndex++, request.getId().toString(), request.getEmployee().getCode(),
                        request.getEmployee().getFullName(),
                        request.getEmployee().getDepartment() == null ? "" : request.getEmployee().getDepartment().getName(),
                        value(request.getStartDate()), value(request.getEndDate()),
                        String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1),
                        request.getReason(), request.getCreatedAt() != null ? request.getCreatedAt().format(DATE_TIME_FORMATTER) : "",
                        leaveStatusLabel(request.getStatus()));
            }
            return toBytes(workbook, sheet, 10);
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportPayrolls(Integer month, Integer year, Long departmentId, Long employeeId, String status) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Bảng lương");
            writeRow(sheet, 0, "Mã nhân viên", "Họ tên", "Phòng ban", "Tháng", "Năm", "Lương cơ bản",
                    "Ngày công tiêu chuẩn", "Ngày công thực tế", "Lương theo ngày công",
                    "Phụ cấp", "Thưởng", "Khấu trừ", "Lương thực nhận", "Trạng thái");
            int rowIndex = 1;
            List<Payroll> payrolls = (month != null && year != null)
                    ? payrollRepository.findByMonthAndYear(month, year)
                    : payrollRepository.findAll();
            payrolls = payrolls.stream()
                    .filter(p -> departmentId == null || (p.getEmployee().getDepartment() != null && departmentId.equals(p.getEmployee().getDepartment().getId())))
                    .filter(p -> employeeId == null || employeeId.equals(p.getEmployee().getId()))
                    .filter(p -> status == null || status.isBlank() || (p.getStatus() != null && p.getStatus().name().equals(status)))
                    .toList();
            for (Payroll payroll : payrolls) {
                writeRow(sheet, rowIndex++, payroll.getEmployee().getCode(), payroll.getEmployee().getFullName(),
                        payroll.getEmployee().getDepartment() == null ? "" : payroll.getEmployee().getDepartment().getName(),
                        value(payroll.getMonth()), value(payroll.getYear()), value(payroll.getBaseSalary()),
                        value(payroll.getStandardWorkDays()), value(payroll.getActualWorkDays()),
                        value(payroll.getAttendanceSalary()), value(payroll.getAllowance()), value(payroll.getBonus()),
                        value(payroll.getDeduction()), value(payroll.getNetSalary()),
                        payroll.getStatus() == Payroll.PayrollStatus.FINALIZED
                                ? "Đã chốt" : "Bản nháp");
            }

            return toBytes(workbook, sheet, 14);
        }
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword.toLowerCase());
    }

    private LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    private boolean overlaps(LocalDate start, LocalDate end, YearMonth period) {
        LocalDate periodStart = period.atDay(1);
        LocalDate periodEnd = period.atEndOfMonth();
        return !end.isBefore(periodStart) && !start.isAfter(periodEnd);
    }

    private byte[] toBytes(Workbook workbook, Sheet sheet, int columnCount) throws IOException {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        workbook.write(output);
        return output.toByteArray();
    }

    private void writeRow(Sheet sheet, int rowIndex, String... values) {
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i] == null ? "" : values[i]);
        }
    }

    private String value(Object value) {
        return value == null ? "" : value.toString();
    }

    private String genderLabel(Employee.Gender gender) {
        if (gender == null) return "";
        return switch (gender) {
            case MALE -> "Nam";
            case FEMALE -> "Nữ";
            case OTHER -> "Khác";
        };
    }

    private String employeeStatusLabel(Employee.EmployeeStatus status) {
        if (status == null) return "";
        return switch (status) {
            case WORKING -> "Đang làm việc";
            case ON_LEAVE -> "Tạm nghỉ";
            case RESIGNED -> "Nghỉ việc";
        };
    }

    private String attendanceStatusLabel(Attendance.AttendanceStatus status) {
        if (status == null) return "";
        return switch (status) {
            case PRESENT -> "Có mặt";
            case LATE -> "Đi muộn";
            case EARLY_LEAVE -> "Về sớm";
            case ABSENT -> "Nghỉ";
            case NOT_CHECKED -> "Chưa chấm công";
        };
    }

    private String leaveStatusLabel(LeaveRequest.LeaveStatus status) {
        if (status == null) return "";
        return switch (status) {
            case PENDING -> "Chờ duyệt";
            case APPROVED -> "Đã duyệt";
            case REJECTED -> "Từ chối";
            case CANCELLED -> "Đã hủy";
        };
    }
}
