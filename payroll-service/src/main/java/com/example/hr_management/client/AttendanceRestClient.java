package com.example.hr_management.client;

import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ServiceUnavailableException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Máy khách REST gọi attendance-service: GET /internal/attendances/summary.
 * Thay cho việc payroll đọc trực tiếp bảng attendances.
 */
@Component
public class AttendanceRestClient {
    /** Dữ liệu chấm công tổng hợp theo kỳ lương. */
    public record AttendanceSummary(int actualDays, int lateDays, int earlyLeaveDays, int absentDays) {
    }

    private final String baseUrl;
    private final String serviceKey;
    private final Duration readTimeout;
    private final HttpClient http;
    private final ObjectMapper mapper = new ObjectMapper();

    public AttendanceRestClient(@Value("${services.attendance.url}") String baseUrl,
                                @Value("${app.service-key:}") String serviceKey,
                                @Value("${services.attendance.connect-timeout-ms:3000}") long connectTimeoutMs,
                                @Value("${services.attendance.read-timeout-ms:5000}") long readTimeoutMs) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.serviceKey = serviceKey;
        this.readTimeout = Duration.ofMillis(readTimeoutMs);
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(connectTimeoutMs)).build();
    }

    public AttendanceSummary summary(long employeeId, int month, int year) {
        if (serviceKey == null || serviceKey.isBlank()) {
            throw new ServiceUnavailableException("Chưa cấu hình SERVICE_API_KEY nên không thể gọi attendance-service");
        }
        URI uri = URI.create(baseUrl + "/internal/attendances/summary?employeeId=" + employeeId
                + "&month=" + month + "&year=" + year);
        try {
            HttpRequest req = HttpRequest.newBuilder(uri)
                    .timeout(readTimeout)
                    .header("Accept", "application/json")
                    .header("X-Service-Key", serviceKey)
                    .GET().build();
            HttpResponse<byte[]> res = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            int status = res.statusCode();
            if (status == 401 || status == 403) {
                throw new ServiceUnavailableException("attendance-service từ chối xác thực giữa các dịch vụ (kiểm tra SERVICE_API_KEY)");
            }
            JsonNode root = mapper.readTree(new String(res.body(), StandardCharsets.UTF_8));
            if (status == 400) {
                throw new BusinessException("attendance-service: " + root.path("message").asText("yêu cầu không hợp lệ"));
            }
            if (status != 200 || !root.path("success").asBoolean(false)) {
                throw new ServiceUnavailableException("attendance-service trả lỗi HTTP " + status);
            }
            JsonNode d = root.path("data");
            return new AttendanceSummary(d.path("actualDays").asInt(), d.path("lateDays").asInt(),
                    d.path("earlyLeaveDays").asInt(), d.path("absentDays").asInt());
        } catch (BusinessException | ServiceUnavailableException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceUnavailableException("Cuộc gọi attendance-service bị ngắt", e);
        } catch (IOException e) {
            throw new ServiceUnavailableException("Không gọi được attendance-service (kết nối/timeout): " + e.getMessage(), e);
        }
    }
}

