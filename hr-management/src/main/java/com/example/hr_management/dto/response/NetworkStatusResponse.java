package com.example.hr_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NetworkStatusResponse {
    private boolean companyNetwork;
    private String message;
}
