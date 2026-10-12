package com.example.hr_management.exception;

/** Một dịch vụ phụ thuộc (hr-core, attendance) không phản hồi hoặc trả dữ liệu không hợp lệ -> HTTP 503. */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

