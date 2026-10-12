package com.example.hr_management.repository;
import com.example.hr_management.entity.Payroll;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
public interface PayrollRepository extends JpaRepository<Payroll,Long> {
    Optional<Payroll> findByEmployeeIdAndMonthAndYear(Long employeeId,Integer month,Integer year);
    List<Payroll> findByMonthAndYear(Integer month, Integer year);
    /** Chỉ truy vấn bảng payrolls (không JOIN sang employees/departments). */
    @Query("select p from Payroll p where (:employeeId is null or p.employeeId=:employeeId) and (:month is null or p.month=:month) and (:year is null or p.year=:year) order by p.year desc,p.month desc,p.employeeId")
    Page<Payroll> search(@Param("employeeId") Long employeeId,@Param("month") Integer month,@Param("year") Integer year,Pageable pageable);
    @Query("select p from Payroll p where (:employeeId is null or p.employeeId=:employeeId) and (:month is null or p.month=:month) and (:year is null or p.year=:year) order by p.year desc,p.month desc,p.employeeId")
    List<Payroll> searchAll(@Param("employeeId") Long employeeId,@Param("month") Integer month,@Param("year") Integer year);
}