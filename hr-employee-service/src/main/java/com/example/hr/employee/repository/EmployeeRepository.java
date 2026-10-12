package com.example.hr.employee.repository;

import com.example.hr.employee.entity.EmployeeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<EmployeeRecord, Long> {
    @Query("select e from EmployeeRecord e where (:keyword is null or lower(e.fullName) like lower(concat('%', :keyword, '%')) or lower(e.code) like lower(concat('%', :keyword, '%')) or lower(e.email) like lower(concat('%', :keyword, '%'))) and (:departmentId is null or e.departmentId = :departmentId) and (:status is null or e.status = :status)")
    Page<EmployeeRecord> search(@Param("keyword") String keyword, @Param("departmentId") Long departmentId, @Param("status") EmployeeRecord.Status status, Pageable pageable);
}
