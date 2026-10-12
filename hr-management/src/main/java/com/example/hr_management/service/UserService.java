package com.example.hr_management.service;

import com.example.hr_management.dto.response.UserResponse;
import com.example.hr_management.entity.User;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponse> getAll(String keyword, int page, int size) {
        return userRepository.findWithFilters(keyword, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    public UserResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public UserResponse lockAccount(Long id) {
        User user = findById(id);
        user.setStatus(User.AccountStatus.LOCKED);
        return toResponse(userRepository.save(user));
    }

    public UserResponse unlockAccount(Long id) {
        User user = findById(id);
        user.setStatus(User.AccountStatus.ACTIVE);
        return toResponse(userRepository.save(user));
    }

    public UserResponse changeRole(Long id, String role) {
        User user = findById(id);
        try {
            user.setRole(User.Role.valueOf(role));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Vai trò không hợp lệ");
        }
        return toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("Mật khẩu xác nhận không khớp");
        }
        if (currentPassword.equals(newPassword)) {
            throw new BusinessException("Mật khẩu mới phải khác mật khẩu hiện tại");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("Mật khẩu hiện tại không chính xác");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với id: " + id));
    }

    private UserResponse toResponse(User u) {
        UserResponse res = new UserResponse();
        res.setId(u.getId());
        res.setUsername(u.getUsername());
        res.setEmail(u.getEmail());
        res.setRole(u.getRole().name());
        res.setStatus(u.getStatus().name());
        if (u.getEmployee() != null) {
            res.setEmployeeId(u.getEmployee().getId());
            res.setEmployeeName(u.getEmployee().getFullName());
        }
        return res;
    }
}
