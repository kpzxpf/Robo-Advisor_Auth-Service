package com.vlz.authservice.service;

import com.vlz.authservice.dto.LoginDto;
import com.vlz.authservice.dto.RegisterDto;
import com.vlz.authservice.entity.User;
import com.vlz.authservice.exception.AuthenticationException;
import com.vlz.authservice.repository.UserRepository;
import com.vlz.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException("Authentication failed");
        }

        return jwtUtil.generateToken(loginDto.getUsername());
    }

    public User register(RegisterDto registerDto) {
        User user = User.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .email(registerDto.getEmail())
                .build();

        return userRepository.save(user);
    }
}
