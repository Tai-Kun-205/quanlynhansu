package com.example.hr_management.service;

import com.example.hr_management.dto.request.PositionRequest;
import com.example.hr_management.dto.response.PositionResponse;
import com.example.hr_management.entity.Position;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    public List<PositionResponse> getAll() {
        return positionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PositionResponse> search(String keyword) {
        return positionRepository.search(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PositionResponse> getByStatus(String status) {
        Position.PositionStatus ps = parseStatus(status);
        return positionRepository.findByStatus(ps).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PositionResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public PositionResponse create(PositionRequest request) {
        if (positionRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Mã chức vụ đã tồn tại");
        }
        if (positionRepository.existsByName(request.getName())) {
            throw new BusinessException("Tên chức vụ đã tồn tại");
        }

        Position position = new Position();
        position.setCode(request.getCode());
        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setStatus(parseStatus(request.getStatus()));

        return toResponse(positionRepository.save(position));
    }

    public PositionResponse update(Long id, PositionRequest request) {
        Position position = findById(id);

        if (positionRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException("Tên chức vụ đã tồn tại");
        }

        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setStatus(parseStatus(request.getStatus()));

        return toResponse(positionRepository.save(position));
    }

    public void delete(Long id) {
        Position position = findById(id);
        positionRepository.delete(position);
    }

    private Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chức vụ với id: " + id));
    }

    private Position.PositionStatus parseStatus(String status) {
        if (status == null) return Position.PositionStatus.ACTIVE;
        try {
            return Position.PositionStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return Position.PositionStatus.ACTIVE;
        }
    }

    private PositionResponse toResponse(Position p) {
        PositionResponse res = new PositionResponse();
        res.setId(p.getId());
        res.setCode(p.getCode());
        res.setName(p.getName());
        res.setDescription(p.getDescription());
        res.setStatus(p.getStatus().name());
        return res;
    }
}

