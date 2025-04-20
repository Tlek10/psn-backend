package com.example.auth_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String bio;
    private String profilePicture;
}
