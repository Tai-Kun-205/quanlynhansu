package com.example.hr_management.service;

import com.example.hr_management.client.AttendanceRestClient;
import com.example.hr_management.client.AttendanceRestClient.AttendanceSummary;
import com.example.hr_management.client.EmployeeSoapClient;
import com.example.hr_management.dto.request.PayrollRequest;
import com.example.hr_management.dto.response.PayrollResponse;
import com.example.hr_management.entity.Payroll;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ServiceUnavailableException;
import com.example.hr_management.repository.PayrollRepository;
import com.example.hr_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** PayrollService chỉ dùng SOAP/REST client (không còn repository employees/attendances) và giữ nguyên công thức lương. */
class PayrollServiceTest {
    private final PayrollRepository payrolls = mock(PayrollRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final EmployeeSoapClient emp = mock(EmployeeSoapClient.class);
    private final AttendanceRestClient att = mock(AttendanceRestClient.class);
    private final PayrollService service = new PayrollService(payrolls, users, emp, att);

    private PayrollRequest request() {
        PayrollRequest r = new PayrollRequest();
        r.setEmployeeId(5L); r.setMonth(10); r.setYear(2026);
        r.setBaseSalary(new BigDecimal("10400000")); r.setStandardWorkDays(26);
        r.setAllowance(new BigDecimal("500000")); r.setBonus(new BigDecimal("200000")); r.setDeduction(new BigDecimal("100000"));
        return r;
    }

    @Test
    void save_usesSoapAndRest_andKeepsFormula() {
        when(emp.getEmployee(5L)).thenReturn(Map.of("id", "5", "code", "NV005", "fullName", "Nguyen Van A", "departmentName", "IT"));
        when(att.summary(5L, 10, 2026)).thenReturn(new AttendanceSummary(20, 2, 1, 3));
        when(payrolls.findByEmployeeIdAndMonthAndYear(5L, 10, 2026)).thenReturn(Optional.empty());
        when(payrolls.save(any(Payroll.class))).thenAnswer(i -> i.getArgument(0));

        PayrollResponse res = service.save(request());

        // 10.400.000 / 26 * 20 = 8.000.000 ; net = 8.000.000 + 500.000 + 200.000 - 100.000 = 8.600.000
        assertEquals(0, new BigDecimal("8000000.00").compareTo(res.getAttendanceSalary()));
        assertEquals(0, new BigDecimal("8600000.00").compareTo(res.getNetSalary()));
        assertEquals(20, res.getActualWorkDays());
        assertEquals(2, res.getLateDays());
        assertEquals(1, res.getEarlyLeaveDays());
        assertEquals(3, res.getAbsentDays());
        assertEquals("NV005", res.getEmployeeCode());
        verify(emp).getEmployee(5L);
        verify(att).summary(5L, 10, 2026);
    }

    @Test
    void save_finalizedPayroll_isRejected() {
        Payroll existing = new Payroll();
        existing.setId(1L); existing.setStatus(Payroll.PayrollStatus.FINALIZED);
        when(emp.getEmployee(5L)).thenReturn(Map.of("id", "5"));
        when(payrolls.findByEmployeeIdAndMonthAndYear(5L, 10, 2026)).thenReturn(Optional.of(existing));
        assertThrows(BusinessException.class, () -> service.save(request()));
        verifyNoInteractions(att);
    }

    @Test
    void search_degradesGracefully_whenHrCoreIsDown() {
        Payroll p = new Payroll();
        p.setId(1L); p.setEmployeeId(7L); p.setMonth(10); p.setYear(2026);
        when(payrolls.search(any(), any(), any(), any())).thenReturn(new PageImpl<>(List.of(p), PageRequest.of(0, 20), 1));
        when(emp.getEmployees(any())).thenThrow(new ServiceUnavailableException("down"));
        PayrollResponse r = service.search(10, 2026, null, 0, 20).getContent().get(0);
        assertEquals("#7", r.getEmployeeCode());
    }

    @Test
    void search_byDepartment_filtersUsingHrCoreData() {
        Payroll a = new Payroll(); a.setId(1L); a.setEmployeeId(1L); a.setMonth(10); a.setYear(2026);
        Payroll b = new Payroll(); b.setId(2L); b.setEmployeeId(2L); b.setMonth(10); b.setYear(2026);
        when(payrolls.searchAll(any(), any(), any())).thenReturn(List.of(a, b));
        when(emp.getEmployees(any())).thenReturn(Map.of(
                1L, Map.of("id", "1", "code", "NV001", "fullName", "A", "departmentId", "3"),
                2L, Map.of("id", "2", "code", "NV002", "fullName", "B", "departmentId", "4")));
        var page = service.search(10, 2026, 3L, 0, 20);
        assertEquals(1, page.getTotalElements());
        assertEquals("NV001", page.getContent().get(0).getEmployeeCode());
    }
}

