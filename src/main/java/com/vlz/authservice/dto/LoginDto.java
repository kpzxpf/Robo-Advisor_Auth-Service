package com.vlz.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@Builder
public class LoginDto {
    @NotBlank(message = "Username cannot be empty")
    @Length(min = 3, max = 50, message = "Username must be between {min} and {max} characters")
    @Pattern(
            regexp = "^[A-Za-z0-9_.-]+$",
            message = "Username can only contain letters, numbers, and the following characters: . - _"
    )
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Length(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).*$",
            message = "Password must contain at least one digit, lowercase letter, uppercase letter, special character and no whitespace"
    )
    private String password;
}