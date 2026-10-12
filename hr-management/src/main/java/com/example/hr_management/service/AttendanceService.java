package com.example.hr_management.service;

import com.example.hr_management.dto.response.AttendanceResponse;
import com.example.hr_management.entity.Attendance;
import com.example.hr_management.entity.Employee;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.AttendanceRepository;
import com.example.hr_management.repository.EmployeeRepository;
import com.example.hr_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NetworkAccessService networkAccessService;

    public AttendanceResponse checkIn(Long employeeId) {
        networkAccessService.requireCompanyNetwork();
        Employee employee = findEmployee(employeeId);
        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByEmployeeIdAndWorkDate(employeeId, today)) {
            Attendance existing = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today).get();
            if (existing.getCheckInTime() != null) {
                throw new BusinessException("Bạn đã chấm công vào hôm nay rồi");
            }
        }

        Attendance attendance = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today)
                .orElse(new Attendance());
        attendance.setEmployee(employee);
        attendance.setWorkDate(today);
        LocalTime now = LocalTime.now();
        attendance.setCheckInTime(now);

        // Standard start time is 8:00
        LocalTime standardStart = LocalTime.of(8, 0);
        if (now.isAfter(standardStart.plusMinutes(15))) {
            attendance.setStatus(Attendance.AttendanceStatus.LATE);
        } else {
            attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
        }

        return toResponse(attendanceRepository.save(attendance));
    }

    public AttendanceResponse checkOut(Long employeeId) {
        networkAccessService.requireCompanyNetwork();
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today)
                .orElseThrow(() -> new BusinessException("Bạn chưa chấm công vào hôm nay"));

        if (attendance.getCheckInTime() == null) {
            throw new BusinessException("Bạn chưa chấm công vào");
        }
        if (attendance.getCheckOutTime() != null) {
            throw new BusinessException("Bạn đã chấm công ra rồi");
        }

        LocalTime now = LocalTime.now();
        attendance.setCheckOutTime(now);

        // Calculate work hours
        double hours = ChronoUnit.MINUTES.between(attendance.getCheckInTime(), now) / 60.0;
        attendance.setWorkHours(Math.round(hours * 100.0) / 100.0);

        // Standard end time is 17:00
        LocalTime standardEnd = LocalTime.of(17, 0);
        if (now.isBefore(standardEnd.minusMinutes(15))) {
            attendance.setStatus(Attendance.AttendanceStatus.EARLY_LEAVE);
        }

        return toResponse(attendanceRepository.save(attendance));
    }

    public Page<AttendanceResponse> getAll(Long employeeId, String startDateStr, String endDateStr,
                                            String status, int page, int size) {
        LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : null;
        LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : null;
        Attendance.AttendanceStatus attendanceStatus = parseStatus(status);

        return attendanceRepository.findWithFilters(employeeId, startDate, endDate, attendanceStatus,
                PageRequest.of(page, size)).map(this::toResponse);
    }

    public AttendanceResponse getById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi chấm công"));
        return toResponse(attendance);
    }

    public Long getEmployeeIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(u -> u.getEmployee() != null ? u.getEmployee().getId() : null)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
    }

    private Attendance.AttendanceStatus parseStatus(String status) {
        if (status == null) return null;
        try {
            return Attendance.AttendanceStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private AttendanceResponse toResponse(Attendance a) {
        AttendanceResponse res = new AttendanceResponse();
        res.setId(a.getId());
        res.setEmployeeId(a.getEmployee().getId());
        res.setEmployeeName(a.getEmployee().getFullName());
        res.setEmployeeCode(a.getEmployee().getCode());
        res.setWorkDate(a.getWorkDate());
        res.setCheckInTime(a.getCheckInTime());
        res.setCheckOutTime(a.getCheckOutTime());
        res.setWorkHours(a.getWorkHours());
        res.setStatus(a.getStatus().name());
        return res;
    }
}
