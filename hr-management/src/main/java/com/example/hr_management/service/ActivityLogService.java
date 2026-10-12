package com.example.hr_management.service;

import com.example.hr_management.dto.request.ActivityLogRequest;
import com.example.hr_management.dto.response.ActivityLogResponse;
import com.example.hr_management.entity.ActivityLog;
import com.example.hr_management.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final ActivityLogRepository repository;

    @Transactional
    public void record(String actor, String action, String module, String targetType, Long targetId,
                       String description, String ipAddress) {
        ActivityLog log = new ActivityLog();
        log.setActorUsername(actor); log.setAction(action); log.setModule(module);
        log.setTargetType(targetType); log.setTargetId(targetId); log.setDescription(description);
        log.setIpAddress(ipAddress); repository.save(log);
    }

    public Page<ActivityLogResponse> search(String keyword, String module, String action, int page, int size) {
        return repository.search(blank(keyword), blank(module), blank(action), PageRequest.of(page, size))
                .map(this::toResponse);
    }

    private String blank(String value) { return value == null || value.isBlank() ? null : value; }
    private ActivityLogResponse toResponse(ActivityLog log) {
        ActivityLogResponse r = new ActivityLogResponse();
        r.setId(log.getId()); r.setActorUsername(log.getActorUsername()); r.setAction(log.getAction());
        r.setModule(log.getModule()); r.setTargetType(log.getTargetType()); r.setTargetId(log.getTargetId());
        r.setDescription(log.getDescription()); r.setCreatedAt(log.getCreatedAt()); r.setIpAddress(log.getIpAddress());
        return r;
    }
}
