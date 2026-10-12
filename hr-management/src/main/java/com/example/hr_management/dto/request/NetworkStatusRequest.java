package com.example.hr_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NetworkStatusRequest {
    @NotNull
    private Boolean companyNetwork;
}
