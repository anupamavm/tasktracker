package com.tasktracker.backend.dto;

import com.tasktracker.backend.model.TaskStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private Long ownerId;
    private String ownerUsername;
}