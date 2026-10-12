package com.example.gateway;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * Cổng vào duy nhất của hệ thống: nhận mọi request từ frontend và chuyển tiếp
 * (nguyên method, header, body) tới service sở hữu đường dẫn đó.
 */
@RestController
public class ProxyController {
    /** Header bị HttpClient cấm tự đặt, hoặc không được chuyển tiếp (hop-by-hop). */
    private static final Set<String> SKIP_REQUEST = Set.of(
            "host", "content-length", "connection", "expect", "upgrade", "transfer-encoding");
    private static final Set<String> SKIP_RESPONSE = Set.of(
            "content-length", "transfer-encoding", "connection");

    private final GatewayRoutes routes;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public ProxyController(GatewayRoutes routes) {
        this.routes = routes;
    }

    @RequestMapping("/**")
    public void forward(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI();
        String target = resolve(path);
        if (target == null) {
            resp.sendError(404, "Gateway: không có service nào xử lý " + path);
            return;
        }
        String query = req.getQueryString();
        URI uri = URI.create(target + path + (query == null ? "" : "?" + query));

        HttpRequest.Builder builder = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(60));
        Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (SKIP_REQUEST.contains(name.toLowerCase())) continue;
            Enumeration<String> values = req.getHeaders(name);
            while (values.hasMoreElements()) builder.header(name, values.nextElement());
        }
        byte[] body = req.getInputStream().readAllBytes();
        builder.method(req.getMethod(), body.length == 0
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(body));

        try {
            HttpResponse<byte[]> r = client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
            resp.setStatus(r.statusCode());
            for (Map.Entry<String, java.util.List<String>> h : r.headers().map().entrySet()) {
                if (h.getKey() == null || SKIP_RESPONSE.contains(h.getKey().toLowerCase())
                        || h.getKey().toLowerCase().startsWith("access-control-")) continue;
                for (String v : h.getValue()) resp.addHeader(h.getKey(), v);
            }
            resp.getOutputStream().write(r.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            resp.sendError(502, "Gateway bị ngắt");
        } catch (IOException e) {
            resp.sendError(503, "Service không phản hồi: " + target);
        }
    }

    /** Chọn route có tiền tố dài nhất khớp với đường dẫn. */
    private String resolve(String path) {
        String best = null;
        for (String prefix : routes.getRoutes().keySet()) {
            String p = prefix.startsWith("/") ? prefix : "/" + prefix;
            if (path.startsWith(p) && (best == null || p.length() > best.length())) best = p;
        }
        if (best == null) return null;
        for (Map.Entry<String, String> e : routes.getRoutes().entrySet()) {
            String p = e.getKey().startsWith("/") ? e.getKey() : "/" + e.getKey();
            if (p.equals(best)) return e.getValue();
        }
        return null;
    }
}

