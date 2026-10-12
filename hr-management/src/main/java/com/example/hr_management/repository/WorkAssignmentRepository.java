package com.example.hr_management.repository;

import com.example.hr_management.entity.WorkAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface WorkAssignmentRepository extends JpaRepository<WorkAssignment, Long> {
    Optional<WorkAssignment> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    @Query("select a from WorkAssignment a join fetch a.employee e join fetch a.shift s " +
           "where (:employeeId is null or e.id = :employeeId) " +
           "and (:shiftId is null or s.id = :shiftId) " +
           "and (:fromDate is null or a.workDate >= :fromDate) " +
           "and (:toDate is null or a.workDate <= :toDate) order by a.workDate desc")
    Page<WorkAssignment> search(@Param("employeeId") Long employeeId, @Param("shiftId") Long shiftId,
                                @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
                                Pageable pageable);
}
