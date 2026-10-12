package com.example.hr_management.service;

import com.example.hr_management.dto.request.WorkShiftRequest;
import com.example.hr_management.dto.response.WorkShiftResponse;
import com.example.hr_management.entity.WorkShift;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.WorkShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkShiftService {
    private final WorkShiftRepository repository;

    public List<WorkShiftResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public WorkShiftResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public WorkShiftResponse create(WorkShiftRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new BusinessException("Mã ca đã tồn tại");
        }
        return toResponse(repository.save(fromRequest(new WorkShift(), request)));
    }

    @Transactional
    public WorkShiftResponse update(Long id, WorkShiftRequest request) {
        if (repository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new BusinessException("Mã ca đã tồn tại");
        }
        return toResponse(repository.save(fromRequest(find(id), request)));
    }

    @Transactional
    public void delete(Long id) {
        WorkShift shift = find(id);
        shift.setActive(false);
        repository.save(shift);
    }

    private WorkShift fromRequest(WorkShift shift, WorkShiftRequest request) {
        shift.setCode(request.getCode());
        shift.setName(request.getName());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setDescription(request.getDescription());
        shift.setActive(request.getActive() == null || request.getActive());
        return shift;
    }

    private WorkShift find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca làm việc"));
    }

    private WorkShiftResponse toResponse(WorkShift shift) {
        WorkShiftResponse response = new WorkShiftResponse();
        response.setId(shift.getId());
        response.setCode(shift.getCode());
        response.setName(shift.getName());
        response.setStartTime(shift.getStartTime());
        response.setEndTime(shift.getEndTime());
        response.setDescription(shift.getDescription());
        response.setActive(shift.isActive());
        return response;
    }
}
