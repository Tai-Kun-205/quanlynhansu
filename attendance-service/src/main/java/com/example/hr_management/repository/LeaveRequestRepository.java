package com.example.hr_management.repository;

import com.example.hr_management.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @Query("SELECT lr FROM LeaveRequest lr " +
           "WHERE (:employeeId IS NULL OR lr.employee.id = :employeeId) " +
           "AND (:status IS NULL OR lr.status = :status) " +
           "ORDER BY lr.createdAt DESC")
    Page<LeaveRequest> findWithFilters(
            @Param("employeeId") Long employeeId,
            @Param("status") LeaveRequest.LeaveStatus status,
            Pageable pageable);

    long countByStatus(LeaveRequest.LeaveStatus status);
}

