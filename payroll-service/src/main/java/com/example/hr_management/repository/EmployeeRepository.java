package com.example.hr_management.repository;

import com.example.hr_management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByCode(String code);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCodeAndIdNot(String code, Long id);
    Optional<Employee> findByCode(String code);

    @Query("SELECT e FROM Employee e " +
           "WHERE (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:departmentId IS NULL OR e.department.id = :departmentId) " +
           "AND (:positionId IS NULL OR e.position.id = :positionId) " +
           "AND (:status IS NULL OR e.status = :status)")
    Page<Employee> findWithFilters(
            @Param("keyword") String keyword,
            @Param("departmentId") Long departmentId,
            @Param("positionId") Long positionId,
            @Param("status") Employee.EmployeeStatus status,
            Pageable pageable);

    long countByDepartmentId(Long departmentId);
    long countByStatus(Employee.EmployeeStatus status);

    @Query("SELECT e.department.id, e.department.name, COUNT(e) FROM Employee e " +
           "WHERE e.department IS NOT NULL GROUP BY e.department.id, e.department.name ORDER BY e.department.name")
    List<Object[]> countGroupedByDepartment();
}
