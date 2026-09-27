package com.task_manager.dto;

import com.task_manager.entity.TaskPriority;
import com.task_manager.entity.TaskStatus;

import java.time.LocalDate;

public record UpdateTaskRequest(String title, String description, TaskStatus status, TaskPriority priority,
                                LocalDate dueDate) {
}
