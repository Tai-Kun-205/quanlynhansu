package com.example.hr_management.client;

import com.example.hr_management.exception.BusinessException;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.exception.ServiceUnavailableException;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/** Chứng minh SOAP request/response: dùng máy chủ giả đóng vai hr-core-service. */
class EmployeeSoapClientTest {
    private static final String NS = "http://example.com/hr/employee";
    private HttpServer server;
    private final AtomicReference<String> lastBody = new AtomicReference<>();
    private final AtomicReference<String> lastKey = new AtomicReference<>();
    private volatile int status = 200;
    private volatile String response = "";
    private volatile long delayMs = 0;

    @BeforeEach
    void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ws", ex -> {
            lastBody.set(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            lastKey.set(ex.getRequestHeaders().getFirst("X-Service-Key"));
            try { Thread.sleep(delayMs); } catch (InterruptedException ignored) { }
            byte[] out = response.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", "text/xml; charset=UTF-8");
            ex.sendResponseHeaders(status, out.length);
            ex.getResponseBody().write(out);
            ex.close();
        });
        server.start();
    }

    @AfterEach
    void stop() {
        server.stop(0);
    }

    private EmployeeSoapClient client(String key, long readTimeoutMs) {
        return new EmployeeSoapClient("http://127.0.0.1:" + server.getAddress().getPort() + "/ws", key, 1000, readTimeoutMs);
    }

    private static String envelope(String body) {
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body>" + body + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

    private static String emp(int id, String code, String name, int dept) {
        return "<ns2:employee xmlns:ns2=\"" + NS + "\"><ns2:id>" + id + "</ns2:id><ns2:code>" + code + "</ns2:code><ns2:fullName>" + name
                + "</ns2:fullName><ns2:email>a@x.com</ns2:email><ns2:departmentName>IT</ns2:departmentName><ns2:status>WORKING</ns2:status><ns2:departmentId>" + dept + "</ns2:departmentId></ns2:employee>";
    }

    @Test
    void getEmployee_sendsSoapRequestWithKey_andParsesResponse() {
        response = envelope("<ns2:getEmployeeResponse xmlns:ns2=\"" + NS + "\">" + emp(5, "NV005", "Nguyen Van A", 2) + "</ns2:getEmployeeResponse>");
        Map<String, String> e = client("secret", 2000).getEmployee(5);
        assertEquals("NV005", e.get("code"));
        assertEquals("Nguyen Van A", e.get("fullName"));
        assertEquals("2", e.get("departmentId"));
        assertTrue(lastBody.get().contains("<emp:getEmployeeRequest><emp:id>5</emp:id></emp:getEmployeeRequest>"));
        assertEquals("secret", lastKey.get());
    }

    @Test
    void getEmployees_batch() {
        response = envelope("<ns2:getEmployeesResponse xmlns:ns2=\"" + NS + "\">" + emp(1, "NV001", "A", 1) + emp(2, "NV002", "B", 1) + "</ns2:getEmployeesResponse>");
        Map<Long, Map<String, String>> r = client("secret", 2000).getEmployees(java.util.List.of(1L, 2L));
        assertEquals(2, r.size());
        assertEquals("B", r.get(2L).get("fullName"));
        assertTrue(lastBody.get().contains("<emp:getEmployeesRequest><emp:id>1</emp:id><emp:id>2</emp:id></emp:getEmployeesRequest>"));
    }

    @Test
    void soapFault_notFound_mapsToResourceNotFound() {
        status = 500;
        response = envelope("<SOAP-ENV:Fault><faultcode>SOAP-ENV:Server</faultcode><faultstring xml:lang=\"en\">Không tìm thấy nhân viên id=99</faultstring></SOAP-ENV:Fault>");
        assertThrows(ResourceNotFoundException.class, () -> client("secret", 2000).getEmployee(99));
    }

    @Test
    void soapFault_other_mapsToBusinessException() {
        status = 500;
        response = envelope("<SOAP-ENV:Fault><faultcode>SOAP-ENV:Server</faultcode><faultstring>boom</faultstring></SOAP-ENV:Fault>");
        BusinessException ex = assertThrows(BusinessException.class, () -> client("secret", 2000).getEmployee(1));
        assertTrue(ex.getMessage().contains("boom"));
    }

    @Test
    void unauthorized_mapsToServiceUnavailable() {
        status = 401;
        response = "{}";
        assertThrows(ServiceUnavailableException.class, () -> client("wrong", 2000).getEmployee(1));
    }

    @Test
    void timeout_mapsToServiceUnavailable() {
        delayMs = 1500;
        response = envelope("");
        assertThrows(ServiceUnavailableException.class, () -> client("secret", 300).getEmployee(1));
    }

    @Test
    void connectionRefused_mapsToServiceUnavailable() {
        server.stop(0);
        assertThrows(ServiceUnavailableException.class, () -> client("secret", 500).getEmployee(1));
    }

    @Test
    void missingKey_failsClosed_withoutCalling() {
        assertThrows(ServiceUnavailableException.class, () -> client("", 500).getEmployee(1));
        assertNull(lastBody.get());
    }
}

