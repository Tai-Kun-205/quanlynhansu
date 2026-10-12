package com.example.hr_management.service;

import com.example.hr_management.dto.request.DepartmentRequest;
import com.example.hr_management.dto.response.DepartmentResponse;
import com.example.hr_management.entity.Department;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.DepartmentRepository;
import com.example.hr_management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DepartmentResponse> search(String keyword) {
        return departmentRepository.search(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DepartmentResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Mã phòng ban đã tồn tại");
        }
        if (departmentRepository.existsByName(request.getName())) {
            throw new BusinessException("Tên phòng ban đã tồn tại");
        }

        Department dept = new Department();
        dept.setCode(request.getCode());
        dept.setName(request.getName());
        dept.setDescription(request.getDescription());
        dept.setStatus(parseStatus(request.getStatus()));

        return toResponse(departmentRepository.save(dept));
    }

    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department dept = findById(id);

        if (departmentRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException("Tên phòng ban đã tồn tại");
        }

        dept.setName(request.getName());
        dept.setDescription(request.getDescription());
        dept.setStatus(parseStatus(request.getStatus()));

        return toResponse(departmentRepository.save(dept));
    }

    public void delete(Long id) {
        Department dept = findById(id);
        long count = employeeRepository.countByDepartmentId(id);
        if (count > 0) {
            throw new BusinessException("Không thể xóa phòng ban vì còn " + count + " nhân viên đang thuộc phòng ban này");
        }
        departmentRepository.delete(dept);
    }

    private Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + id));
    }

    private Department.DepartmentStatus parseStatus(String status) {
        if (status == null) return Department.DepartmentStatus.ACTIVE;
        try {
            return Department.DepartmentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return Department.DepartmentStatus.ACTIVE;
        }
    }

    private DepartmentResponse toResponse(Department dept) {
        DepartmentResponse res = new DepartmentResponse();
        res.setId(dept.getId());
        res.setCode(dept.getCode());
        res.setName(dept.getName());
        res.setDescription(dept.getDescription());
        res.setStatus(dept.getStatus().name());
        res.setEmployeeCount(employeeRepository.countByDepartmentId(dept.getId()));
        return res;
    }
}

