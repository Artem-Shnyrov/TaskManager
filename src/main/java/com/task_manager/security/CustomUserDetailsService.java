package com.task_manager.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.task_manager.repository.UserRepository;
import com.task_manager.entity.User;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor 
public class CustomUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return new CustomUserDetails(user);
    }
}
