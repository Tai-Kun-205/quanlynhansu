package com.example.hr_management.service;

import com.example.hr_management.dto.request.EmployeeRequest;
import com.example.hr_management.dto.request.EmployeeProfileRequest;
import com.example.hr_management.dto.response.EmployeeResponse;
import com.example.hr_management.entity.Department;
import com.example.hr_management.entity.Employee;
import com.example.hr_management.entity.Position;
import com.example.hr_management.entity.User;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.DepartmentRepository;
import com.example.hr_management.repository.EmployeeRepository;
import com.example.hr_management.repository.PositionRepository;
import com.example.hr_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<EmployeeResponse> getAll(String keyword, Long departmentId, Long positionId,
                                          String status, int page, int size) {
        Employee.EmployeeStatus empStatus = parseStatus(status);
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findWithFilters(keyword, departmentId, positionId, empStatus, pageable)
                .map(this::toResponse);
    }

    public EmployeeResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Mã nhân viên đã tồn tại");
        }
        if (request.getEmail() != null && employeeRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email đã tồn tại");
        }

        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban"));
        Position pos = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chức vụ"));

        Employee employee = new Employee();
        employee.setCode(request.getCode());
        employee.setFullName(request.getFullName());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(parseGender(request.getGender()));
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setAddress(request.getAddress());
        applyExtendedProfile(employee, request);
        employee.setStartDate(request.getStartDate());
        employee.setDepartment(dept);
        employee.setPosition(pos);
        employee.setStatus(Employee.EmployeeStatus.WORKING);

        Employee saved = employeeRepository.save(employee);

        // Create user account if provided
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException("Tên đăng nhập đã tồn tại");
            }
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(
                    request.getPassword() != null && !request.getPassword().isBlank()
                            ? request.getPassword() : "123456"));
            user.setEmail(request.getEmail());
            user.setRole(User.Role.EMPLOYEE);
            user.setStatus(User.AccountStatus.ACTIVE);
            user.setEmployee(saved);
            userRepository.save(user);
        }

        return toResponse(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findById(id);

        if (request.getEmail() != null && employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new BusinessException("Email đã tồn tại");
        }

        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban"));
        Position pos = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chức vụ"));

        employee.setFullName(request.getFullName());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(parseGender(request.getGender()));
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setAddress(request.getAddress());
        applyExtendedProfile(employee, request);
        employee.setStartDate(request.getStartDate());
        employee.setDepartment(dept);
        employee.setPosition(pos);
        if (request.getStatus() != null) {
            employee.setStatus(parseStatus(request.getStatus()));
        }

        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = findById(id);
        employeeRepository.delete(employee);
    }

    public EmployeeResponse updateProfile(Long id, EmployeeProfileRequest request) {
        Employee employee = findById(id);
        // Employee can only update personal info
        if (request.getFullName() != null) employee.setFullName(request.getFullName());
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getAddress() != null) employee.setAddress(request.getAddress());
        if (request.getDateOfBirth() != null) employee.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) employee.setGender(parseGender(request.getGender()));
        if (request.getAvatarUrl() != null) employee.setAvatarUrl(request.getAvatarUrl());
        if (request.getEmergencyContactName() != null) employee.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null) employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        if (request.getEmergencyContactRelationship() != null) employee.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
        if (request.getEducationLevel() != null) employee.setEducationLevel(request.getEducationLevel());
        if (request.getMajor() != null) employee.setMajor(request.getMajor());
        return toResponse(employeeRepository.save(employee));
    }

    public Long getEmployeeIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
        if (user.getEmployee() == null) {
            throw new ResourceNotFoundException("Tài khoản chưa liên kết nhân viên");
        }
        return user.getEmployee().getId();
    }

    private void applyExtendedProfile(Employee employee, EmployeeRequest request) {
        employee.setAvatarUrl(request.getAvatarUrl());
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        employee.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
        employee.setEducationLevel(request.getEducationLevel());
        employee.setMajor(request.getMajor());
    }

    private Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với id: " + id));
    }

    private Employee.EmployeeStatus parseStatus(String status) {
        if (status == null) return null;
        try {
            return Employee.EmployeeStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Employee.Gender parseGender(String gender) {
        if (gender == null) return null;
        try {
            return Employee.Gender.valueOf(gender);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public EmployeeResponse toResponse(Employee e) {
        EmployeeResponse res = new EmployeeResponse();
        res.setId(e.getId());
        res.setCode(e.getCode());
        res.setFullName(e.getFullName());
        res.setDateOfBirth(e.getDateOfBirth());
        res.setGender(e.getGender() != null ? e.getGender().name() : null);
        res.setPhone(e.getPhone());
        res.setEmail(e.getEmail());
        res.setAddress(e.getAddress());
        res.setAvatarUrl(e.getAvatarUrl());
        res.setEmergencyContactName(e.getEmergencyContactName());
        res.setEmergencyContactPhone(e.getEmergencyContactPhone());
        res.setEmergencyContactRelationship(e.getEmergencyContactRelationship());
        res.setEducationLevel(e.getEducationLevel());
        res.setMajor(e.getMajor());
        res.setStartDate(e.getStartDate());
        res.setSeniorityYears(e.getStartDate() == null ? null :
                Math.max(0, Period.between(e.getStartDate(), LocalDate.now()).getYears()));
        if (e.getDepartment() != null) {
            res.setDepartmentId(e.getDepartment().getId());
            res.setDepartmentName(e.getDepartment().getName());
        }
        if (e.getPosition() != null) {
            res.setPositionId(e.getPosition().getId());
            res.setPositionName(e.getPosition().getName());
        }
        res.setStatus(e.getStatus().name());
        if (e.getUser() != null) {
            res.setUserId(e.getUser().getId());
            res.setUsername(e.getUser().getUsername());
        }
        return res;
    }
}
