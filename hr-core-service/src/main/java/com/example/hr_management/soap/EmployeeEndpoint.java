package com.example.hr_management.soap;

import com.example.hr_management.entity.Employee;
import com.example.hr_management.exception.ResourceNotFoundException;
import com.example.hr_management.repository.EmployeeRepository;
import com.example.hr_management.soap.EmployeeSoapMessages.EmployeeInfo;
import com.example.hr_management.soap.EmployeeSoapMessages.GetEmployeeRequest;
import com.example.hr_management.soap.EmployeeSoapMessages.GetEmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/** Dịch vụ SOAP EmployeeService: các service khác tra cứu thông tin nhân viên qua đây. */
@Endpoint
@RequiredArgsConstructor
public class EmployeeEndpoint {
    private final EmployeeRepository employeeRepository;

    @PayloadRoot(namespace = EmployeeSoapMessages.NAMESPACE, localPart = "getEmployeeRequest")
    @ResponsePayload
    @Transactional(readOnly = true)
    public GetEmployeeResponse getEmployee(@RequestPayload GetEmployeeRequest request) {
        Employee e = employeeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên id=" + request.getId()));
        GetEmployeeResponse response = new GetEmployeeResponse();
        response.setEmployee(toInfo(e));
        return response;
    }

    /** Lay thong tin nhieu nhan vien trong mot loi goi (toi da 500 id; id khong ton tai duoc bo qua). */
    @PayloadRoot(namespace = EmployeeSoapMessages.NAMESPACE, localPart = "getEmployeesRequest")
    @ResponsePayload
    @Transactional(readOnly = true)
    public EmployeeSoapMessages.GetEmployeesResponse getEmployees(@RequestPayload EmployeeSoapMessages.GetEmployeesRequest request) {
        if (request.getId() == null || request.getId().isEmpty() || request.getId().size() > 500) {
            throw new com.example.hr_management.exception.BusinessException("Danh sach id phai co tu 1 den 500 phan tu");
        }
        EmployeeSoapMessages.GetEmployeesResponse response = new EmployeeSoapMessages.GetEmployeesResponse();
        for (Employee e : employeeRepository.findAllById(request.getId())) {
            response.getEmployee().add(toInfo(e));
        }
        return response;
    }

    private EmployeeInfo toInfo(Employee e) {
        EmployeeInfo info = new EmployeeInfo();
        info.setId(e.getId());
        info.setCode(e.getCode());
        info.setFullName(e.getFullName());
        info.setEmail(e.getEmail());
        info.setDepartmentName(e.getDepartment() == null ? null : e.getDepartment().getName());
        info.setDepartmentId(e.getDepartment() == null ? null : e.getDepartment().getId());
        info.setPositionName(e.getPosition() == null ? null : e.getPosition().getName());
        info.setStatus(e.getStatus() == null ? null : e.getStatus().name());
        return info;
    }
}

