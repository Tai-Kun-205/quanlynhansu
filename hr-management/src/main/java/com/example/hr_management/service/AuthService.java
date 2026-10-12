package com.example.hr_management.service;

import com.example.hr_management.dto.request.LoginRequest;
import com.example.hr_management.dto.response.LoginResponse;
import com.example.hr_management.entity.User;
import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.repository.UserRepository;
import com.example.hr_management.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại"));

        if (user.getStatus() == User.AccountStatus.LOCKED) {
            throw new BusinessException("Tài khoản đã bị khóa");
        }

        String token = jwtUtil.generateToken(userDetails);
        Long employeeId = user.getEmployee() != null ? user.getEmployee().getId() : null;

        return new LoginResponse(token, user.getUsername(), user.getRole().name(), user.getId(), employeeId);
    }
}

