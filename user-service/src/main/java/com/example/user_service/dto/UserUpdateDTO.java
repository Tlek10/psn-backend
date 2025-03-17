package com.example.user_service.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String fullName;
    private String bio;
    private String profilePicture;
    private String username;
    private String email;
}