package com.example.hr_management.service;

import com.example.hr_management.dto.request.WorkAssignmentRequest;
import com.example.hr_management.dto.response.WorkAssignmentResponse;
import com.example.hr_management.entity.Employee;
import com.example.hr_management.entity.WorkAssignment;
import com.example.hr_management.entity.WorkShift;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.EmployeeRepository;
import com.example.hr_management.repository.WorkAssignmentRepository;
import com.example.hr_management.repository.WorkShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WorkAssignmentService {
    private final WorkAssignmentRepository repository;
    private final EmployeeRepository employeeRepository;
    private final WorkShiftRepository shiftRepository;

    public Page<WorkAssignmentResponse> search(Long employeeId, Long shiftId, String from, String to,
                                                int page, int size) {
        LocalDate fromDate = from == null || from.isBlank() ? null : LocalDate.parse(from);
        LocalDate toDate = to == null || to.isBlank() ? null : LocalDate.parse(to);
        return repository.search(employeeId, shiftId, fromDate, toDate, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Transactional
    public WorkAssignmentResponse save(Long id, WorkAssignmentRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
        WorkShift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca làm việc"));
        WorkAssignment assignment = id == null ? new WorkAssignment() : repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch làm việc"));
        repository.findByEmployeeIdAndWorkDate(request.getEmployeeId(), request.getWorkDate())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new BusinessException("Nhân viên đã được phân ca trong ngày này"); });
        assignment.setEmployee(employee);
        assignment.setShift(shift);
        assignment.setWorkDate(request.getWorkDate());
        assignment.setNote(request.getNote());
        return toResponse(repository.save(assignment));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Không tìm thấy lịch làm việc");
        repository.deleteById(id);
    }

    private WorkAssignmentResponse toResponse(WorkAssignment a) {
        WorkAssignmentResponse r = new WorkAssignmentResponse();
        r.setId(a.getId());
        r.setEmployeeId(a.getEmployee().getId());
        r.setEmployeeCode(a.getEmployee().getCode());
        r.setEmployeeName(a.getEmployee().getFullName());
        r.setShiftId(a.getShift().getId());
        r.setShiftCode(a.getShift().getCode());
        r.setShiftName(a.getShift().getName());
        r.setStartTime(a.getShift().getStartTime().toString());
        r.setEndTime(a.getShift().getEndTime().toString());
        r.setWorkDate(a.getWorkDate());
        r.setNote(a.getNote());
        return r;
    }
}
