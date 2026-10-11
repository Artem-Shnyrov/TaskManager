package com.task_manager.controller;

import com.task_manager.dto.CreateTaskRequest;
import com.task_manager.dto.TaskResponse;
import com.task_manager.dto.UpdateTaskRequest;
import com.task_manager.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return taskService.createTask(request, userDetails.getUsername());
    }

    @GetMapping
    public List<TaskResponse> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        return taskService.getAll(userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return taskService.getById(id, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        taskService.deleteTaskBtId(id,  userDetails.getUsername());
    }

    @PatchMapping("/{id}")
    public TaskResponse updateTask(@RequestBody UpdateTaskRequest request, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return taskService.updateTask(id, request, userDetails.getUsername());
    }
}
