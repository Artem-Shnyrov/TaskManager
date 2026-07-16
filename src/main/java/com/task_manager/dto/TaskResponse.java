package com.task_manager.dto;

import com.task_manager.entity.TaskPriority;
import com.task_manager.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(Long id, String title, String description, TaskStatus status, TaskPriority priority, Long projectId, Long ownerId, LocalDate dueDate, Instant createdAt) {
}
