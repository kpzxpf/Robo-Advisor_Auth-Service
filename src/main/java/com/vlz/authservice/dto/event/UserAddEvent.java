package com.vlz.authservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserAddEvent {
    private String username;
    private String password;
    private String email;
}
