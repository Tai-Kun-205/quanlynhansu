package com.example.hr.employee.service;

import com.example.hr.employee.dto.EmployeeResponse;
import com.example.hr.employee.entity.EmployeeRecord;
import com.example.hr.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeQueryService {
    private final EmployeeRepository repository;

    public Page<EmployeeResponse> search(String keyword, Long departmentId, String status, int page, int size) {
        EmployeeRecord.Status parsed = status == null || status.isBlank() ? null : EmployeeRecord.Status.valueOf(status);
        return repository.search(blank(keyword), departmentId, parsed, PageRequest.of(page, Math.min(size, 100)))
                .map(this::toResponse);
    }

    public EmployeeResponse getById(Long id) {
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private String blank(String value) { return value == null || value.isBlank() ? null : value; }

    private EmployeeResponse toResponse(EmployeeRecord employee) {
        return new EmployeeResponse(employee.getId(), employee.getCode(), employee.getFullName(), employee.getDateOfBirth(),
                employee.getGender() == null ? null : employee.getGender().name(), employee.getPhone(), employee.getEmail(),
                employee.getAddress(), employee.getAvatarUrl(), employee.getStartDate(), employee.getDepartmentId(),
                employee.getPositionId(), employee.getStatus() == null ? null : employee.getStatus().name());
    }

    public static class EmployeeNotFoundException extends RuntimeException {
        public EmployeeNotFoundException(Long id) { super("Không tìm thấy nhân viên với id: " + id); }
    }
}
