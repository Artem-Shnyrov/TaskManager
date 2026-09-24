package com.task_manager.service;

import com.task_manager.dto.RegisterRequest;
import com.task_manager.entity.Role;
import com.task_manager.entity.User;
import com.task_manager.exception.ResourceAlreadyExistsException;
import com.task_manager.repository.UserRepository;
import com.task_manager.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_hashesPasswordBeforeSaving() {
        RegisterRequest registerRequest = new RegisterRequest("a@b.com", "plainPassword", "Test");

        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtUtil.generateToken(registerRequest.email())).thenReturn(registerRequest.email());

        authService.register(registerRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User user = captor.getValue();

        assertEquals("hashedPassword", user.getPassword());
        assertNotEquals("plainPassword", user.getPassword());
    }

    @Test
    void register_duplicateEmailThrowsException() {
        RegisterRequest registerRequest = new RegisterRequest("a@b.com", "plainPassword", "Test");

        User user = User.builder()
                .id(1L)
                .role(Role.USER)
                .email("a@b.com")
                .build();

        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.of(user));

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(registerRequest));
    }
}
