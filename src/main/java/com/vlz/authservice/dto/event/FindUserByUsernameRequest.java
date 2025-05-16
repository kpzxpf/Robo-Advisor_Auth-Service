package com.vlz.authservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindUserByUsernameRequest {
    private String username;
}
