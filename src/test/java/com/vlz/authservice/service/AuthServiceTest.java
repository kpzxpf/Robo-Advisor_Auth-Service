package com.vlz.authservice.service;

import com.vlz.authservice.dto.LoginDto;
import com.vlz.authservice.exception.AuthenticationException;
import com.vlz.authservice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void testLogin_successfulAuthentication_shouldReturnToken() {
        // Arrange
        LoginDto loginDto = LoginDto.builder()
                .username("validuser")
                .password("Valid@123")
                .build();

        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.isAuthenticated()).thenReturn(true);

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        Mockito.when(jwtUtil.generateToken("validuser")).thenReturn("validToken");
        Mockito.when(jwtUtil.validateToken("validToken")).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> authService.login(loginDto));
    }

    @Test
    public void testLogin_invalidCredentials_shouldThrowAuthenticationException() {
        // Arrange
        LoginDto loginDto = LoginDto.builder()
                .username("invaliduser")
                .password("Invalid@123")
                .build();

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> authService.login(loginDto));
    }

    @Test
    public void testLogin_authenticationNotAuthenticated_shouldThrowAuthenticationException() {
        // Arrange
        LoginDto loginDto = LoginDto.builder()
                .username("anotheruser")
                .password("AnotherValid@123")
                .build();

        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.isAuthenticated()).thenReturn(false);

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> authService.login(loginDto));
    }

    @Test
    public void testLogin_tokenGenerationFails_shouldThrowAuthenticationException() {
        // Arrange
        LoginDto loginDto = LoginDto.builder()
                .username("tokenfailuser")
                .password("TokenFail@123")
                .build();

        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.isAuthenticated()).thenReturn(true);

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        Mockito.when(jwtUtil.generateToken("tokenfailuser")).thenReturn("invalidToken");
        Mockito.when(jwtUtil.validateToken("invalidToken")).thenReturn(false);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> authService.login(loginDto));
    }
}