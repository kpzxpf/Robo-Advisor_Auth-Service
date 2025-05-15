package com.vlz.authservice.controller;

import com.vlz.authservice.dto.LoginDto;
import com.vlz.authservice.dto.RegisterDto;
import com.vlz.authservice.dto.event.UserSavedEvent;
import com.vlz.authservice.mapper.UserMapper;
import com.vlz.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/register")
    public UserSavedEvent register(@RequestBody @Valid RegisterDto registerDto) {
        return authService.register(registerDto);
    }
}
