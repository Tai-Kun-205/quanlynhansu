package com.example.hr_management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/**
 * Xac thuc giua cac dich vu (service-to-service): cac duong dan noi bo (SOAP /ws, REST /internal)
 * chi duoc goi khi header X-Service-Key khop khoa cau hinh app.service-key (bien moi truong SERVICE_API_KEY).
 * Chua cau hinh khoa => tu choi tat ca (fail closed). Khoa khong bao gio duoc ghi log hay tra ve client.
 */
@Component
public class ServiceKeyFilter extends OncePerRequestFilter {
    public static final String HEADER = "X-Service-Key";

    private final byte[] expected;

    public ServiceKeyFilter(@Value("${app.service-key:}") String serviceKey) {
        this.expected = serviceKey == null ? new byte[0] : serviceKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return !(p.startsWith("/ws/") || p.equals("/ws") || p.startsWith("/internal/"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String provided = request.getHeader(HEADER);
        if (expected.length > 0 && provided != null
                && MessageDigest.isEqual(expected, provided.getBytes(StandardCharsets.UTF_8))) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    "internal-service", null, List.of(new SimpleGrantedAuthority("ROLE_SERVICE"))));
        }
        chain.doFilter(request, response);
    }
}