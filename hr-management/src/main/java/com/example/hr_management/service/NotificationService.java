package com.example.hr_management.service;

import com.example.hr_management.dto.request.NotificationRequest;
import com.example.hr_management.dto.response.NotificationResponse;
import com.example.hr_management.entity.*;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notifications;
    private final NotificationReadRepository reads;
    private final UserRepository users;
    private final DepartmentRepository departments;

    public List<NotificationResponse> forUser(String username) {
        User user = user(username);
        return notifications.findByActiveTrueOrderByCreatedAtDesc().stream()
                .filter(n -> n.getDepartment() == null || (user.getEmployee() != null &&
                        user.getEmployee().getDepartment() != null &&
                        n.getDepartment().getId().equals(user.getEmployee().getDepartment().getId())))
                .map(n -> response(n, user)).toList();
    }

    public List<NotificationResponse> all() {
        return notifications.findByActiveTrueOrderByCreatedAtDesc().stream().map(n -> response(n, null)).toList();
    }

    @Transactional
    public NotificationResponse create(NotificationRequest request, String username) {
        Notification n = new Notification();
        n.setTitle(request.getTitle()); n.setContent(request.getContent()); n.setCreatedBy(username);
        if (request.getDepartmentId() != null) n.setDepartment(departments.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban")));
        return response(notifications.save(n), null);
    }

    @Transactional
    public NotificationResponse update(Long id, NotificationRequest request) {
        Notification n = notifications.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        n.setTitle(request.getTitle()); n.setContent(request.getContent());
        n.setDepartment(request.getDepartmentId() == null ? null : departments.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban")));
        return response(notifications.save(n), null);
    }

    @Transactional
    public void delete(Long id) {
        Notification n = notifications.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        n.setActive(false); notifications.save(n);
    }

    @Transactional
    public void markRead(Long id, String username) {
        User user = user(username);
        Notification n = notifications.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        if (!visibleTo(n, user)) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền đọc thông báo này");
        }
        if (reads.findByNotificationIdAndUserId(id, user.getId()).isEmpty()) {
            reads.save(new NotificationRead(null, n, user, java.time.LocalDateTime.now()));
        }
    }

    public long unreadCount(String username) {
        User user = user(username);
        return forUser(username).stream().filter(n -> !n.isRead()).count();
    }

    @Transactional
    public void markAllRead(String username) {
        User user = user(username);
        notifications.findByActiveTrueOrderByCreatedAtDesc().stream()
                .filter(n -> visibleTo(n, user))
                .filter(n -> reads.findByNotificationIdAndUserId(n.getId(), user.getId()).isEmpty())
                .forEach(n -> reads.save(new NotificationRead(null, n, user, java.time.LocalDateTime.now())));
    }

    private boolean visibleTo(Notification n, User user) {
        return n.getDepartment() == null || (user.getEmployee() != null
                && user.getEmployee().getDepartment() != null
                && n.getDepartment().getId().equals(user.getEmployee().getDepartment().getId()));
    }

    private User user(String username) { return users.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản")); }
    private NotificationResponse response(Notification n, User user) {
        NotificationResponse r = new NotificationResponse(); r.setId(n.getId()); r.setTitle(n.getTitle());
        r.setContent(n.getContent()); r.setCreatedBy(n.getCreatedBy()); r.setCreatedAt(n.getCreatedAt());
        r.setDepartmentName(n.getDepartment() == null ? "Toàn công ty" : n.getDepartment().getName());
        r.setRead(user != null && reads.findByNotificationIdAndUserId(n.getId(), user.getId()).isPresent());
        return r;
    }
}
