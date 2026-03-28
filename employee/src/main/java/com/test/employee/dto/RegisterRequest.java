package com.test.employee.dto;

import com.test.employee.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {


    @NotBlank(message = "Can't be empty")
    @Size(min = 2, max=50, message = "Full name must have at least 2 or maximum 50 characters")
    private String fullName;

    @NotBlank(message = "Required")
    @Size(min = 4, max=10, message = "Full name must have at least 4 or maximum 10 characters")
    private String username;

    @NotBlank(message = "Can't be empty")
    @Size(min = 2, message = "Full name must have at least 5 characters")
    private String password;

    private Role role;
}
