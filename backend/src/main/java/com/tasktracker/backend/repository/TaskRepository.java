package com.tasktracker.backend.repository;

import com.tasktracker.backend.model.Task;
import com.tasktracker.backend.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE " +
            "(:ownerId IS NULL OR t.owner.id = :ownerId) AND " +
            "(:status IS NULL OR t.status = :status)")
    Page<Task> findByFilters(@Param("ownerId") Long ownerId,
                             @Param("status") TaskStatus status,
                             Pageable pageable);
}