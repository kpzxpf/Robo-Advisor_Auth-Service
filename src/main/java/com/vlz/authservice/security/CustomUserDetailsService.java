package com.vlz.authservice.security;

import com.vlz.authservice.dto.event.FindUserByUsernameRequest;
import com.vlz.authservice.entity.User;
import com.vlz.authservice.kafkaRequest.UserFindByUsernameKafkaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserFindByUsernameKafkaRequest userFindByUsernameKafkaRequest;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userFindByUsernameKafkaRequest
                .findUserByUsernameRequest(new FindUserByUsernameRequest(username)).getUser();

        var authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getRoleName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}