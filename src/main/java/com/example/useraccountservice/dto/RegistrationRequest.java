package com.example.useraccountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {

    @NotNull(message =  "Email is required")
    private String email;
    @NotNull(message =  "Password is required")
    private String password;
    @NotNull(message =  "First Name is required")
    private String firstName;
    @NotNull(message =  "Last Name is required")
    private String lastName;
    private String role;
}
