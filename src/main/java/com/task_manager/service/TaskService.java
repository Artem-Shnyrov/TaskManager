package com.task_manager.service;

import com.task_manager.dto.CreateTaskRequest;
import com.task_manager.dto.TaskResponse;
import com.task_manager.entity.Project;
import com.task_manager.entity.Task;
import com.task_manager.entity.TaskStatus;
import com.task_manager.entity.User;
import com.task_manager.repository.ProjectRepository;
import com.task_manager.repository.TaskRepository;
import com.task_manager.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Project project = null;
        if (request.projectId() != null) {
            project = projectRepository.findById(request.projectId()).orElseThrow(() -> new RuntimeException("Project not found"));
        }

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .status(TaskStatus.TODO)
                .priority(request.priority())
                .owner(owner)
                .project(project)
                .build();
        Task savedTask = taskRepository.save(task);
        return toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(Long id){
        return toResponse(taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found")));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAll(){
        return taskRepository.findAll().stream().map(this::toResponse).toList();
    }

    private TaskResponse toResponse(Task task) {
        Long projectId = (task.getProject() != null) ? task.getProject().getId() : null;
         return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                projectId,
                task.getOwner().getId(),
                task.getDueDate(),
                task.getCreatedAt());
    }

}
