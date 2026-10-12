package com.example.hr_management.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

/** Các message của hợp đồng SOAP trong resources/xsd/employee.xsd. */
public final class EmployeeSoapMessages {
    public static final String NAMESPACE = "http://example.com/hr/employee";

    private EmployeeSoapMessages() {
    }

    @Data
    @XmlRootElement(name = "getEmployeeRequest", namespace = NAMESPACE)
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", namespace = NAMESPACE, propOrder = {"id"})
    public static class GetEmployeeRequest {
        private long id;
    }

    @Data
    @XmlRootElement(name = "getEmployeeResponse", namespace = NAMESPACE)
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", namespace = NAMESPACE, propOrder = {"employee"})
    public static class GetEmployeeResponse {
        private EmployeeInfo employee;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "employeeInfo", namespace = NAMESPACE,
            propOrder = {"id", "code", "fullName", "email", "departmentName", "positionName", "status", "departmentId"})
    public static class EmployeeInfo {
        private long id;
        private String code;
        private String fullName;
        private String email;
        private String departmentName;
        private String positionName;
        private String status;
        private Long departmentId;
    }

    @Data
    @XmlRootElement(name = "getEmployeesRequest", namespace = NAMESPACE)
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", namespace = NAMESPACE, propOrder = {"id"})
    public static class GetEmployeesRequest {
        @XmlElement(name = "id", namespace = NAMESPACE)
        private java.util.List<Long> id = new java.util.ArrayList<>();
    }

    @Data
    @XmlRootElement(name = "getEmployeesResponse", namespace = NAMESPACE)
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", namespace = NAMESPACE, propOrder = {"employee"})
    public static class GetEmployeesResponse {
        @XmlElement(name = "employee", namespace = NAMESPACE)
        private java.util.List<EmployeeInfo> employee = new java.util.ArrayList<>();
    }
}

