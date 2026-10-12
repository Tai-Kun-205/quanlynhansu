package com.example.hr_management.controller;

import com.example.hr_management.client.EmployeeSoapClient;
import com.example.hr_management.client.EmployeeSoapClient.SoapExchange;
import com.example.hr_management.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/** Tích hợp SOA: payroll-service lấy thông tin nhân viên từ hr-core-service bằng SOAP. */
@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollEmployeeInfoController {
    private final EmployeeSoapClient employeeSoapClient;

    @GetMapping("/employee-info/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, String>> employeeInfo(@PathVariable long employeeId) {
        return ApiResponse.success(employeeSoapClient.getEmployee(employeeId));
    }

    /**
     * Demo SOA: thực hiện một lời gọi SOAP thật tới hr-core-service và trả về kết quả cùng XML request/response.
     * Khóa X-Service-Key không nằm trong XML trả về (nó chỉ ở HTTP header); email được che trước khi hiển thị.
     */
    @GetMapping("/soap-demo/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> soapDemo(@PathVariable long employeeId) {
        SoapExchange x = employeeSoapClient.callGetEmployee(employeeId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("provider", "hr-core-service");
        out.put("consumer", "payroll-service");
        out.put("operation", "getEmployeeRequest");
        out.put("endpoint", x.endpoint());
        out.put("elapsedMs", x.elapsedMs());
        out.put("authentication", "Header X-Service-Key (giá trị được ẩn, không gửi lên trình duyệt)");
        out.put("requestXml", x.requestXml());
        out.put("responseXml", maskEmail(x.responseXml()));
        Map<String, String> employee = new LinkedHashMap<>(employeeSoapClient.firstEmployee(x.responseXml()));
        employee.computeIfPresent("email", (k, v) -> mask(v));
        out.put("employee", employee);
        return ApiResponse.success("Đã gọi SOAP tới hr-core-service", out);
    }

    static String maskEmail(String xml) {
        return xml.replaceAll("(<[^>/]*email>)([^<]*)(</)", "$1***$3");
    }

    static String mask(String email) {
        int at = email.indexOf('@');
        return at <= 1 ? "***" : email.charAt(0) + "***" + email.substring(at);
    }
}
