package com.dalv.bksims.models.dtos.auth;

import com.dalv.bksims.models.entities.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Code cannot be blank")
    private String code;
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "Gender cannot be blank")
    private String gender;
    @NotBlank(message = "Date of birth cannot be blank")
    private String dob;
    @NotBlank(message = "Email cannot be blank")
    private String email;
    @NotNull(message = "Role cannot be null")
    private Role role;
    @NotBlank(message = "Phone cannot be blank")
    private String phone;
}
