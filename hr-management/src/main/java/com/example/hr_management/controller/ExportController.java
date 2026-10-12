package com.example.hr_management.controller;

import com.example.hr_management.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/exports")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ExportController {

    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private final ExcelExportService excelExportService;

    @GetMapping("/employees")
    public ResponseEntity<ByteArrayResource> employees(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long positionId,
            @RequestParam(required = false) String status) throws IOException {
        return fileResponse(excelExportService.exportEmployees(keyword, departmentId, positionId, status), "danh-sach-nhan-vien.xlsx");
    }

    @GetMapping("/attendances")
    public ResponseEntity<ByteArrayResource> attendances(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status) throws IOException {
        String suffix = startDate != null && startDate.length() >= 7 ? "-thang-" + startDate.substring(5, 7) + "-" + startDate.substring(0, 4) : "";
        return fileResponse(excelExportService.exportAttendances(employeeId, departmentId, startDate, endDate, status),
                "cham-cong" + suffix + ".xlsx");
    }

    @GetMapping("/leave-requests")
    public ResponseEntity<ByteArrayResource> leaveRequests(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) throws IOException {
        String suffix = month != null && year != null ? "-thang-" + month + "-" + year : "";
        return fileResponse(excelExportService.exportLeaveRequests(employeeId, departmentId, status, month, year),
                "nghi-phep" + suffix + ".xlsx");
    }

    @GetMapping("/payrolls")
    public ResponseEntity<ByteArrayResource> payrolls(
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer month,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status) throws IOException {
        String suffix = month != null && year != null ? "-thang-" + month + "-" + year : "";
        return fileResponse(excelExportService.exportPayrolls(month, year, departmentId, employeeId, status),
                "bang-luong" + suffix + ".xlsx");
    }

    private ResponseEntity<ByteArrayResource> fileResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .contentType(XLSX)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .contentLength(bytes.length)
                .body(new ByteArrayResource(bytes));
    }
}
