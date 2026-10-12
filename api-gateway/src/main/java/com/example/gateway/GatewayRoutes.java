package com.example.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/** Bảng định tuyến: tiền tố đường dẫn -> URL của service (cấu hình ở application.properties). */
@ConfigurationProperties(prefix = "gateway")
public class GatewayRoutes {
    private Map<String, String> routes = new LinkedHashMap<>();

    public Map<String, String> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, String> routes) {
        this.routes = routes;
    }
}

