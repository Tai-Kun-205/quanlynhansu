package com.example.hr_management.service;

import com.example.hr_management.client.AttendanceRestClient;
import com.example.hr_management.client.AttendanceRestClient.AttendanceSummary;
import com.example.hr_management.client.EmployeeSoapClient;
import com.example.hr_management.dto.request.PayrollRequest;
import com.example.hr_management.dto.response.PayrollResponse;
import com.example.hr_management.entity.Payroll;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.exception.ServiceUnavailableException;
import com.example.hr_management.repository.PayrollRepository;
import com.example.hr_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Nghiệp vụ lương. Dữ liệu nhân viên lấy từ hr-core-service qua SOAP (EmployeeSoapClient),
 * dữ liệu chấm công lấy từ attendance-service qua REST (AttendanceRestClient);
 * service này KHÔNG còn truy vấn bảng employees / attendances.
 */
@Service
@RequiredArgsConstructor
public class PayrollService {
    private final PayrollRepository payrolls;
    private final UserRepository users;
    private final EmployeeSoapClient employeeClient;
    private final AttendanceRestClient attendanceClient;

    public Page<PayrollResponse> search(Integer month, Integer year, Long dept, int page, int size) {
        if (dept == null) {
            Page<Payroll> result = payrolls.search(null, month, year, PageRequest.of(page, size));
            Map<Long, Map<String, String>> infos = lookupTolerant(result.getContent());
            return result.map(p -> toResponse(p, infos.get(p.getEmployeeId())));
        }
        // Lọc theo phòng ban: phòng ban thuộc hr-core-service nên phải hỏi hr-core trước, sau đó phân trang thủ công.
        List<Payroll> all = payrolls.searchAll(null, month, year);
        Map<Long, Map<String, String>> infos = employeeClient.getEmployees(ids(all));
        List<PayrollResponse> filtered = all.stream()
                .filter(p -> infos.get(p.getEmployeeId()) != null
                        && String.valueOf(dept).equals(infos.get(p.getEmployeeId()).get("departmentId")))
                .map(p -> toResponse(p, infos.get(p.getEmployeeId())))
                .collect(Collectors.toList());
        int from = Math.min(page * size, filtered.size());
        int to = Math.min(from + size, filtered.size());
        return new PageImpl<>(filtered.subList(from, to), PageRequest.of(page, size), filtered.size());
    }

    public Page<PayrollResponse> my(String username, int page, int size) {
        Long id = users.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"))
                .getEmployee().getId();
        Page<Payroll> result = payrolls.search(id, null, null, PageRequest.of(page, size));
        Map<Long, Map<String, String>> infos = lookupTolerant(result.getContent());
        return result.map(p -> toResponse(p, infos.get(p.getEmployeeId())));
    }

    @Transactional
    public PayrollResponse save(PayrollRequest r) {
        // 1) Nhân viên: hỏi hr-core-service qua SOAP (không tồn tại -> 404)
        Map<String, String> employee = employeeClient.getEmployee(r.getEmployeeId());

        Payroll p = payrolls.findByEmployeeIdAndMonthAndYear(r.getEmployeeId(), r.getMonth(), r.getYear())
                .orElse(new Payroll());
        if (p.getId() != null && p.getStatus() == Payroll.PayrollStatus.FINALIZED) {
            throw new BusinessException("Bảng lương đã chốt, không thể chỉnh sửa");
        }

        // 2) Chấm công: hỏi attendance-service qua REST
        AttendanceSummary a = attendanceClient.summary(r.getEmployeeId(), r.getMonth(), r.getYear());
        int actual = a.actualDays();

        // 3) Công thức lương giữ nguyên
        BigDecimal base = z(r.getBaseSalary());
        BigDecimal attendanceSalary = base.divide(BigDecimal.valueOf(r.getStandardWorkDays()), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(actual)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal allowance = z(r.getAllowance()), bonus = z(r.getBonus()), deduction = z(r.getDeduction());

        p.setEmployeeId(r.getEmployeeId());
        p.setMonth(r.getMonth());
        p.setYear(r.getYear());
        p.setBaseSalary(base);
        p.setStandardWorkDays(r.getStandardWorkDays());
        p.setActualWorkDays(actual);
        p.setAttendanceSalary(attendanceSalary);
        p.setLateDays(a.lateDays());
        p.setEarlyLeaveDays(a.earlyLeaveDays());
        p.setAbsentDays(a.absentDays());
        p.setAllowance(allowance);
        p.setBonus(bonus);
        p.setDeduction(deduction);
        p.setNetSalary(attendanceSalary.add(allowance).add(bonus).subtract(deduction).setScale(2, RoundingMode.HALF_UP));
        return toResponse(payrolls.save(p), employee);
    }

    @Transactional
    public void finalize(Long id) {
        Payroll p = payrolls.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bảng lương"));
        p.setStatus(Payroll.PayrollStatus.FINALIZED);
        payrolls.save(p);
    }

    private Set<Long> ids(List<Payroll> list) {
        return list.stream().map(Payroll::getEmployeeId).collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    /** Khi hr-core-service lỗi, vẫn hiển thị bảng lương (chỉ thiếu tên/mã nhân viên) thay vì sập cả màn hình. */
    private Map<Long, Map<String, String>> lookupTolerant(List<Payroll> list) {
        try {
            return employeeClient.getEmployees(ids(list));
        } catch (ServiceUnavailableException e) {
            return Collections.emptyMap();
        }
    }

    private BigDecimal z(BigDecimal x) {
        return x == null ? BigDecimal.ZERO : x;
    }

    private PayrollResponse toResponse(Payroll p, Map<String, String> emp) {
        PayrollResponse r = new PayrollResponse();
        r.setId(p.getId());
        r.setEmployeeId(p.getEmployeeId());
        r.setEmployeeCode(emp == null ? "#" + p.getEmployeeId() : emp.get("code"));
        r.setEmployeeName(emp == null ? "(chưa lấy được từ hr-core-service)" : emp.get("fullName"));
        r.setDepartmentName(emp == null ? null : emp.get("departmentName"));
        r.setMonth(p.getMonth());
        r.setYear(p.getYear());
        r.setStandardWorkDays(p.getStandardWorkDays());
        r.setActualWorkDays(p.getActualWorkDays());
        r.setLateDays(p.getLateDays());
        r.setEarlyLeaveDays(p.getEarlyLeaveDays());
        r.setAbsentDays(p.getAbsentDays());
        r.setBaseSalary(p.getBaseSalary());
        r.setAttendanceSalary(p.getAttendanceSalary());
        r.setAllowance(p.getAllowance());
        r.setBonus(p.getBonus());
        r.setDeduction(p.getDeduction());
        r.setNetSalary(p.getNetSalary());
        r.setStatus(p.getStatus().name());
        return r;
    }
}
