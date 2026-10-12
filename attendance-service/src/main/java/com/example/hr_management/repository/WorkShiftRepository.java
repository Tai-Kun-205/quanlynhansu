package com.example.hr_management.repository;

import com.example.hr_management.entity.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    Optional<WorkShift> findByCode(String code);
}
