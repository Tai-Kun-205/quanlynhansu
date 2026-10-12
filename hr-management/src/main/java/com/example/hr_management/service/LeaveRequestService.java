package com.example.hr_management.service;

import com.example.hr_management.dto.request.LeaveRequestDto;
import com.example.hr_management.dto.response.LeaveRequestResponse;
import com.example.hr_management.entity.Employee;
import com.example.hr_management.entity.LeaveRequest;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.EmployeeRepository;
import com.example.hr_management.repository.LeaveRequestRepository;
import com.example.hr_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public LeaveRequestResponse create(Long employeeId, LeaveRequestDto dto) {
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BusinessException("Ngày bắt đầu không được sau ngày kết thúc");
        }
        if (dto.getStartDate().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException("Ngày bắt đầu không được là ngày trong quá khứ");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        LeaveRequest lr = new LeaveRequest();
        lr.setEmployee(employee);
        lr.setStartDate(dto.getStartDate());
        lr.setEndDate(dto.getEndDate());
        lr.setReason(dto.getReason());
        lr.setStatus(LeaveRequest.LeaveStatus.PENDING);

        return toResponse(leaveRequestRepository.save(lr));
    }

    public Page<LeaveRequestResponse> getAll(Long employeeId, String status, int page, int size) {
        LeaveRequest.LeaveStatus ls = parseStatus(status);
        return leaveRequestRepository.findWithFilters(employeeId, ls, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    public LeaveRequestResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public LeaveRequestResponse approve(Long id) {
        LeaveRequest lr = findById(id);
        if (lr.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new BusinessException("Chỉ có thể duyệt đơn đang ở trạng thái chờ duyệt");
        }
        lr.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        return toResponse(leaveRequestRepository.save(lr));
    }

    public LeaveRequestResponse reject(Long id) {
        LeaveRequest lr = findById(id);
        if (lr.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new BusinessException("Chỉ có thể từ chối đơn đang ở trạng thái chờ duyệt");
        }
        lr.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        return toResponse(leaveRequestRepository.save(lr));
    }

    public LeaveRequestResponse cancel(Long id, Long employeeId) {
        LeaveRequest lr = findById(id);
        if (!lr.getEmployee().getId().equals(employeeId)) {
            throw new BusinessException("Bạn không có quyền hủy đơn này");
        }
        if (lr.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new BusinessException("Chỉ có thể hủy đơn đang ở trạng thái chờ duyệt");
        }
        lr.setStatus(LeaveRequest.LeaveStatus.CANCELLED);
        return toResponse(leaveRequestRepository.save(lr));
    }

    public Long getEmployeeIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(u -> u.getEmployee() != null ? u.getEmployee().getId() : null)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
    }

    private LeaveRequest findById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn nghỉ phép"));
    }

    private LeaveRequest.LeaveStatus parseStatus(String status) {
        if (status == null) return null;
        try {
            return LeaveRequest.LeaveStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private LeaveRequestResponse toResponse(LeaveRequest lr) {
        LeaveRequestResponse res = new LeaveRequestResponse();
        res.setId(lr.getId());
        res.setEmployeeId(lr.getEmployee().getId());
        res.setEmployeeName(lr.getEmployee().getFullName());
        res.setEmployeeCode(lr.getEmployee().getCode());
        res.setStartDate(lr.getStartDate());
        res.setEndDate(lr.getEndDate());
        res.setReason(lr.getReason());
        res.setCreatedAt(lr.getCreatedAt());
        res.setStatus(lr.getStatus().name());
        return res;
    }
}

