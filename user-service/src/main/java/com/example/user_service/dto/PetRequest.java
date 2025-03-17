package com.example.user_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PetRequest {
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private String photo;
}