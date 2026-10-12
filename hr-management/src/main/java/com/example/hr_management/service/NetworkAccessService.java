package com.example.hr_management.service;

import com.example.hr_management.dto.response.NetworkStatusResponse;
import com.example.hr_management.exception.CompanyNetworkRequiredException;
import org.springframework.stereotype.Service;

@Service
public class NetworkAccessService {

    private static final String ACCESS_DENIED_MESSAGE =
            "Bạn phải kết nối mạng công ty để chấm công.";

    private volatile boolean companyNetwork = true;

    public boolean isCompanyNetwork() {
        return companyNetwork;
    }

    public NetworkStatusResponse getStatus() {
        return new NetworkStatusResponse(companyNetwork, statusMessage());
    }

    public NetworkStatusResponse updateStatus(boolean companyNetwork) {
        this.companyNetwork = companyNetwork;
        return getStatus();
    }

    public void requireCompanyNetwork() {
        if (!companyNetwork) {
            throw new CompanyNetworkRequiredException(ACCESS_DENIED_MESSAGE);
        }
    }

    private String statusMessage() {
        return companyNetwork ? "Đang kết nối mạng công ty" : "Ngoài mạng công ty";
    }
}
