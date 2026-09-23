package com.task_manager.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.task_manager.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class CustomUserDetails implements UserDetails{
    private final User user;

    @Override 
    public String getUsername(){
        return user.getEmail();
    }

    @Override
    public String getPassword(){
        return user.getPassword();
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }
}
