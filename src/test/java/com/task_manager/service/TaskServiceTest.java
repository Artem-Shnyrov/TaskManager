package com.task_manager.service;

import com.task_manager.dto.CreateTaskRequest;
import com.task_manager.dto.TaskResponse;
import com.task_manager.entity.*;
import com.task_manager.exception.ResourceNotFoundException;
import com.task_manager.repository.ProjectRepository;
import com.task_manager.repository.TaskRepository;
import com.task_manager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_setsStatusToTodo_andReturnsResponse() {
        User user = User.builder()
                .id(1L)
                .email("a@b.com")
                .name("Test user")
                .role(Role.USER)
                .build();

        Task savedTask = Task.builder()
                .id(1L)
                .title("Buy milk")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .owner(user)
                .build();

        CreateTaskRequest request = new CreateTaskRequest("Buy milk", null, null, TaskPriority.MEDIUM, null);

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponse response = taskService.createTask(request, "a@b.com");

        assertNotNull(response);
        assertEquals(TaskStatus.TODO, response.status());
        assertEquals("Buy milk", response.title());
        assertEquals(1L, response.id());
    }

    @Test
    void createTask_throwsWhenOwnerNotFound() {
        CreateTaskRequest request = new CreateTaskRequest("Buy milk", null, null, TaskPriority.MEDIUM, null);

        when(userRepository.findByEmail("missing@b.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.createTask(request, "missing@b.com"));
    }

    @Test
    void getById_returnTask(){
        User user = User.builder()
                .id(1L)
                .email("a@b.com")
                .name("Test")
                .role(Role.USER)
                .build();

        Task task = Task.builder()
                .id(5L)
                .title("Buy milk")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .owner(user)
                .build();

        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getById(5L);

        assertEquals(5L, response.id());
        assertEquals("Buy milk", response.title());
    }

    @Test
    void getById_throwsWhenUserNotFound() {
        when(taskRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getById(5L));
    }

    @Test
    void getAll_returnsAllTasks() {
        User user = User.builder().id(1L).email("a@b.com").name("Test").role(Role.USER).build();
        Task task1 = Task.builder().id(1L).title("First").status(TaskStatus.TODO).priority(TaskPriority.LOW).owner(user).build();
        Task task2 = Task.builder().id(2L).title("Second").status(TaskStatus.DONE).priority(TaskPriority.HIGH).owner(user).build();

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));

        List<TaskResponse> responses = taskService.getAll();

        assertEquals(2, responses.size());
        assertEquals("First", responses.get(0).title());
    }
}
