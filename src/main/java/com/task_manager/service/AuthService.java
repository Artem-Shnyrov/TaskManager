package com.task_manager.service;

import com.task_manager.dto.AuthResponse;
import com.task_manager.dto.LoginRequest;
import com.task_manager.dto.RegisterRequest;
import com.task_manager.entity.Role;
import com.task_manager.entity.User;
import com.task_manager.exception.ResourceAlreadyExistsException;
import com.task_manager.repository.UserRepository;
import com.task_manager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest){
        userRepository.findByEmail(registerRequest.email())
                .ifPresent(user -> {throw new ResourceAlreadyExistsException("Email already exists");});

        String hashedPassword  = passwordEncoder.encode(registerRequest.password());

        User userEntity = User.builder()
                .email(registerRequest.email())
                .password(hashedPassword)
                .name(registerRequest.name())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(userEntity);

        String token = jwtUtil.generateToken(savedUser.getEmail());
        return new AuthResponse(token, savedUser.getEmail());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        String token = jwtUtil.generateToken(loginRequest.email());
        return new AuthResponse(token, loginRequest.email());
    }
}
