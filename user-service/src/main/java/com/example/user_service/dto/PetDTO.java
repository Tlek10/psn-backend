package com.example.user_service.dto;

import com.example.user_service.model.Pet;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PetDTO {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private String photo;

    public PetDTO(Pet pet) {
        this.id = pet.getId();
        this.name = pet.getName();
        this.species = pet.getSpecies();
        this.breed = pet.getBreed();
        this.birthDate = pet.getBirthDate();
        this.photo = pet.getPhoto();
    }
}