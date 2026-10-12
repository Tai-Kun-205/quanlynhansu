package com.example.hr_management.service;

import com.example.hr_management.dto.response.AttendanceSummaryResponse;
import com.example.hr_management.entity.Attendance;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.repository.AttendanceRepository;
import com.example.hr_management.repository.EmployeeRepository;
import com.example.hr_management.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AttendanceSummaryTest {
    private final AttendanceRepository repo = mock(AttendanceRepository.class);
    private final AttendanceService service = new AttendanceService(repo, mock(EmployeeRepository.class),
            mock(UserRepository.class), mock(NetworkAccessService.class));

    private Attendance a(Attendance.AttendanceStatus s) {
        Attendance x = new Attendance();
        x.setStatus(s);
        x.setWorkDate(LocalDate.of(2026, 10, 1));
        return x;
    }

    @Test
    void summarize_countsByStatus() {
        List<Attendance> list = new ArrayList<>();
        for (int i = 0; i < 18; i++) list.add(a(Attendance.AttendanceStatus.PRESENT));
        list.add(a(Attendance.AttendanceStatus.LATE));
        list.add(a(Attendance.AttendanceStatus.LATE));
        list.add(a(Attendance.AttendanceStatus.EARLY_LEAVE));
        list.add(a(Attendance.AttendanceStatus.ABSENT));
        list.add(a(Attendance.AttendanceStatus.NOT_CHECKED));
        when(repo.findByEmployeeIdAndWorkDateBetween(eq(5L), any(), any())).thenReturn(list);
        AttendanceSummaryResponse r = service.summarize(5L, 10, 2026);
        assertEquals(21, r.getActualDays());   // 18 + 2 LATE + 1 EARLY_LEAVE
        assertEquals(2, r.getLateDays());
        assertEquals(1, r.getEarlyLeaveDays());
        assertEquals(1, r.getAbsentDays());
    }

    @Test
    void summarize_validatesInput() {
        assertThrows(BusinessException.class, () -> service.summarize(0L, 10, 2026));
        assertThrows(BusinessException.class, () -> service.summarize(5L, 13, 2026));
        assertThrows(BusinessException.class, () -> service.summarize(5L, 10, 1999));
        assertThrows(BusinessException.class, () -> service.summarize(null, 10, 2026));
    }
}

