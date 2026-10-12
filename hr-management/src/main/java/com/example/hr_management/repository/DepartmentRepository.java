package com.example.hr_management.repository;

import com.example.hr_management.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsByCode(String code);
    Optional<Department> findByCode(String code);
    List<Department> findByNameContainingIgnoreCase(String name);
    @Query("SELECT d FROM Department d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Department> search(String keyword);
}

