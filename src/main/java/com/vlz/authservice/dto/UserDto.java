package com.vlz.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "Username cannot be empty")
    @Length(min = 3, max = 50, message = "Username must be between {min} and {max} characters")
    @Pattern(
            regexp = "^[A-Za-z0-9_.-]+$",
            message = "Username can only contain letters, numbers, and the following characters: . - _"
    )
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Please provide a valid email address"
    )
    @Size(max = 100, message = "Email cannot be longer than {max} characters")
    private String email;

    @Pattern(
            regexp = "^[A-Za-zА-Яа-я\\s-]*$",
            message = "First name can only contain letters, spaces and hyphens"
    )
    @Size(max = 50, message = "First name cannot be longer than {max} characters")
    private String firstName;

    @Pattern(
            regexp = "^[A-Za-zА-Яа-я\\s-]*$",
            message = "Last name can only contain letters, spaces and hyphens"
    )
    @Size(max = 50, message = "Last name cannot be longer than {max} characters")
    private String lastName;

}
