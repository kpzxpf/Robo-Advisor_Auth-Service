package com.vlz.authservice.controller;

import com.vlz.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final JwtUtil jwtUtil;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        return jwtUtil.getJwkSet();
    }
}
