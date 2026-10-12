package com.example.hr_management.client;

import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Máy khách SOAP gọi EmployeeService của hr-core-service (hợp đồng: employee.xsd / employees.wsdl).
 * URL, khóa xác thực và timeout đều lấy từ cấu hình (application.properties / biến môi trường).
 */
@Component
public class EmployeeSoapClient {
    private static final String NS = "http://example.com/hr/employee";
    private static final String SERVICE_KEY_HEADER = "X-Service-Key";

    /** Kết quả một lần gọi SOAP (dùng cho cả nghiệp vụ lẫn màn hình demo). */
    public record SoapExchange(String endpoint, String requestXml, String responseXml, long elapsedMs) {
    }

    private final String endpoint;
    private final String serviceKey;
    private final Duration readTimeout;
    private final HttpClient http;

    public EmployeeSoapClient(@Value("${services.hr-core.soap-url}") String endpoint,
                              @Value("${app.service-key:}") String serviceKey,
                              @Value("${services.hr-core.connect-timeout-ms:3000}") long connectTimeoutMs,
                              @Value("${services.hr-core.read-timeout-ms:5000}") long readTimeoutMs) {
        this.endpoint = endpoint;
        this.serviceKey = serviceKey;
        this.readTimeout = Duration.ofMillis(readTimeoutMs);
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(connectTimeoutMs)).build();
    }

    /** Lấy 1 nhân viên theo id. */
    public Map<String, String> getEmployee(long id) {
        return firstEmployee(callGetEmployee(id).responseXml());
    }

    /** Đọc nhân viên đầu tiên trong XML phản hồi. */
    public Map<String, String> firstEmployee(String responseXml) {
        return parseEmployees(responseXml).stream().findFirst()
                .orElseThrow(() -> new ServiceUnavailableException("Phản hồi SOAP không hợp lệ từ hr-core-service"));
    }

    /** Gọi getEmployeeRequest và trả về cả XML request/response. */
    public SoapExchange callGetEmployee(long id) {
        return send("<emp:getEmployeeRequest><emp:id>" + id + "</emp:id></emp:getEmployeeRequest>");
    }

    /** Lấy nhiều nhân viên trong một lần gọi (getEmployeesRequest). Id không tồn tại sẽ không có trong kết quả. */
    public Map<Long, Map<String, String>> getEmployees(Collection<Long> ids) {
        Map<Long, Map<String, String>> result = new LinkedHashMap<>();
        if (ids == null || ids.isEmpty()) return result;
        StringBuilder body = new StringBuilder("<emp:getEmployeesRequest>");
        for (Long id : ids) body.append("<emp:id>").append(id).append("</emp:id>");
        body.append("</emp:getEmployeesRequest>");
        for (Map<String, String> e : parseEmployees(send(body.toString()).responseXml())) {
            result.put(Long.valueOf(e.get("id")), e);
        }
        return result;
    }

    private SoapExchange send(String bodyXml) {
        if (serviceKey == null || serviceKey.isBlank()) {
            throw new ServiceUnavailableException("Chưa cấu hình SERVICE_API_KEY nên không thể gọi hr-core-service");
        }
        String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:emp=\"" + NS + "\">"
                + "<soapenv:Header/><soapenv:Body>" + bodyXml + "</soapenv:Body></soapenv:Envelope>";
        long start = System.currentTimeMillis();
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(endpoint))
                    .timeout(readTimeout)
                    .header("Content-Type", "text/xml; charset=UTF-8")
                    .header(SERVICE_KEY_HEADER, serviceKey)
                    .POST(HttpRequest.BodyPublishers.ofString(envelope, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<byte[]> res = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (res.statusCode() == 401 || res.statusCode() == 403) {
                throw new ServiceUnavailableException("hr-core-service từ chối xác thực giữa các dịch vụ (kiểm tra SERVICE_API_KEY)");
            }
            String xml = new String(res.body(), StandardCharsets.UTF_8);
            long elapsed = System.currentTimeMillis() - start;
            Document doc = parse(res.body());
            NodeList fault = doc.getElementsByTagNameNS("*", "faultstring");
            if (fault.getLength() > 0) {
                String msg = fault.item(0).getTextContent();
                if (msg != null && msg.startsWith("Không tìm thấy")) {
                    throw new com.example.hr_management.exception.ResourceNotFoundException(msg);
                }
                throw new BusinessException("hr-core-service: " + msg);
            }
            return new SoapExchange(endpoint, envelope, xml, elapsed);
        } catch (BusinessException | ServiceUnavailableException | com.example.hr_management.exception.ResourceNotFoundException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceUnavailableException("Cuộc gọi SOAP bị ngắt", e);
        } catch (IOException e) {
            throw new ServiceUnavailableException("Không gọi được hr-core-service qua SOAP (kết nối/timeout): " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Phản hồi SOAP không hợp lệ từ hr-core-service", e);
        }
    }

    private java.util.List<Map<String, String>> parseEmployees(String xml) {
        try {
            Document doc = parse(xml.getBytes(StandardCharsets.UTF_8));
            NodeList employees = doc.getElementsByTagNameNS(NS, "employee");
            java.util.List<Map<String, String>> list = new java.util.ArrayList<>();
            for (int i = 0; i < employees.getLength(); i++) {
                Map<String, String> m = new LinkedHashMap<>();
                NodeList children = employees.item(i).getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node n = children.item(j);
                    if (n.getNodeType() == Node.ELEMENT_NODE) m.put(n.getLocalName(), n.getTextContent());
                }
                list.add(m);
            }
            return list;
        } catch (Exception e) {
            throw new ServiceUnavailableException("Phản hồi SOAP không hợp lệ từ hr-core-service", e);
        }
    }

    private static Document parse(byte[] xml) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return f.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
    }
}
