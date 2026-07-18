package com.task_manager.dto;

import com.task_manager.entity.TaskPriority;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateTaskRequest(@NotBlank String title, String description, LocalDate dueDate, @NotNull TaskPriority priority, Long projectId, @NotNull Long ownerId) {
}
