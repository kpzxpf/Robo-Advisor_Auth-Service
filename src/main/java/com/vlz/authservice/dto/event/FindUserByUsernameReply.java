package com.vlz.authservice.dto.event;

import com.vlz.authservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserByUsernameReply {
    private User user;
    private boolean success;
    private String message;
}
