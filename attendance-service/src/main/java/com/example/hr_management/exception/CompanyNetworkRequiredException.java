package com.example.hr_management.exception;

public class CompanyNetworkRequiredException extends RuntimeException {
    public CompanyNetworkRequiredException(String message) {
        super(message);
    }
}
