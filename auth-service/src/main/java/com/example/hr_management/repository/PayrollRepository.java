package com.example.hr_management.repository;
import com.example.hr_management.entity.Payroll;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PayrollRepository extends JpaRepository<Payroll,Long> {
    Optional<Payroll> findByEmployeeIdAndMonthAndYear(Long employeeId,Integer month,Integer year);
    java.util.List<Payroll> findByMonthAndYear(Integer month, Integer year);
    @org.springframework.data.jpa.repository.Query("select p from Payroll p join fetch p.employee e where (:employeeId is null or e.id=:employeeId) and (:month is null or p.month=:month) and (:year is null or p.year=:year) and (:departmentId is null or e.department.id=:departmentId) order by p.year desc,p.month desc,e.fullName")
    Page<Payroll> search(@org.springframework.data.repository.query.Param("employeeId") Long employeeId,@org.springframework.data.repository.query.Param("month") Integer month,@org.springframework.data.repository.query.Param("year") Integer year,@org.springframework.data.repository.query.Param("departmentId") Long departmentId,Pageable pageable);
}
