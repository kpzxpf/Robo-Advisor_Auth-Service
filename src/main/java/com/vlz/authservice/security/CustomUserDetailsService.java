package com.vlz.authservice.security;

import com.vlz.authservice.dto.event.FindUserByUsernameRequest;
import com.vlz.authservice.entity.User;
import com.vlz.authservice.kafkaGetaway.UserFindByUsernameKafkaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserFindByUsernameKafkaGateway userFindByUsernameKafkaGateway;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userFindByUsernameKafkaGateway
                .findUserByIdRequest(new FindUserByUsernameRequest(username));

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