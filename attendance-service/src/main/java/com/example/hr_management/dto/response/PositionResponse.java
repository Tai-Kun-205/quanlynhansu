package com.example.hr_management.dto.response;

import lombok.Data;

@Data
public class PositionResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String status;
}

