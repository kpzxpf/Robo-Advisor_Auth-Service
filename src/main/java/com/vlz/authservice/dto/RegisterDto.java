package com.vlz.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@Builder
public class RegisterDto {
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
            message = "Password must contain at least one digit, lowercase letter, uppercase letter," +
                    " special character and no whitespace"
    )
    private String password;

    @NotBlank(message = "Email cannot be empty")
    @Email(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Please provide a valid email address"
    )
    @Size(max = 100, message = "Email cannot be longer than {max} characters")
    private String email;
}
