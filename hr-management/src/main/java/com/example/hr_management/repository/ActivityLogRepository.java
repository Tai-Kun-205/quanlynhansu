package com.example.hr_management.repository;

import com.example.hr_management.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    @Query("select a from ActivityLog a where (:keyword is null or lower(a.actorUsername) like lower(concat('%',:keyword,'%')) or lower(a.description) like lower(concat('%',:keyword,'%'))) and (:module is null or a.module=:module) and (:action is null or a.action=:action) order by a.createdAt desc")
    Page<ActivityLog> search(@Param("keyword") String keyword, @Param("module") String module,
                             @Param("action") String action, Pageable pageable);
}
