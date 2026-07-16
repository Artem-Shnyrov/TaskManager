package com.task_manager.dto;

import com.task_manager.entity.TaskPriority;

import java.time.LocalDate;

public record CreateTaskRequest(String title, String description, LocalDate dueDate, TaskPriority priority, Long projectId, Long ownerId) {
}
