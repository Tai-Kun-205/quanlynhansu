package com.example.hr_management.repository;

import com.example.hr_management.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
    boolean existsByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
    List<Attendance> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);
    List<Attendance> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a " +
           "WHERE (:employeeId IS NULL OR a.employee.id = :employeeId) " +
           "AND (:startDate IS NULL OR a.workDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.workDate <= :endDate) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "ORDER BY a.workDate DESC")
    Page<Attendance> findWithFilters(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Attendance.AttendanceStatus status,
            Pageable pageable);

    long countByWorkDate(LocalDate workDate);
    long countByWorkDateAndStatus(LocalDate workDate, Attendance.AttendanceStatus status);
}
