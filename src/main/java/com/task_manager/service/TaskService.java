package com.task_manager.service;

import com.task_manager.dto.CreateTaskRequest;
import com.task_manager.dto.TaskResponse;
import com.task_manager.dto.UpdateTaskRequest;
import com.task_manager.entity.Project;
import com.task_manager.entity.Task;
import com.task_manager.entity.TaskStatus;
import com.task_manager.entity.User;
import com.task_manager.exception.ResourceNotFoundException;
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
    public TaskResponse createTask(CreateTaskRequest request, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Project project = null;
        if (request.projectId() != null) {
            project = projectRepository.findById(request.projectId()).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
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

    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request, String email) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOwner().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Task not found");
        }

        if(request.title() != null) {
            task.setTitle(request.title());
        }

        if(request.description() != null) {
            task.setDescription(request.description());
        }

        if(request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        if(request.priority() != null) {
            task.setPriority(request.priority());
        }

        if(request.status() != null) {
            task.setStatus(request.status());
        }

        taskRepository.save(task);
        return toResponse(task);
    }

    @Transactional
    public void deleteTaskBtId(Long id, String email) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!task.getOwner().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Task not found");
        }
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(Long id, String email){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOwner().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Task not found");
        }

        return toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAll(String email){
        return taskRepository.findByOwnerEmail(email).stream().map(this::toResponse).toList();
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
