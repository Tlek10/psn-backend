package com.example.user_service.service;

import com.example.user_service.dto.PetDTO;
import com.example.user_service.dto.PetRequest;
import com.example.user_service.exception.custom.DuplicateEntityException;
import com.example.user_service.exception.custom.InvalidOperationException;
import com.example.user_service.model.Pet;
import com.example.user_service.model.User;
import com.example.user_service.repository.PetRepository;
import com.example.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addPet(String username, PetRequest petRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean petExists = user.getPets().stream()
                .anyMatch(pet -> pet.getName().equalsIgnoreCase(petRequest.getName()));

        if (petExists) {
            throw new DuplicateEntityException("Pet with this name already exists");
        }

        Pet pet = new Pet();
        pet.setName(petRequest.getName());
        pet.setSpecies(petRequest.getSpecies());
        pet.setBreed(petRequest.getBreed());
        pet.setBirthDate(petRequest.getBirthDate());
        pet.setPhoto(petRequest.getPhoto());
        pet.setOwner(user);

        petRepository.save(pet);
        user.getPets().add(pet);
        userRepository.save(user);
    }

    public List<PetDTO> getUserPets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getPets().stream()
                .map(PetDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePet(String username, Long petId, PetRequest petRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (!pet.getOwner().getId().equals(user.getId())) {
            throw new InvalidOperationException("Pet does not belong to this user");
        }

        if (petRequest.getName() != null) pet.setName(petRequest.getName());
        if (petRequest.getSpecies() != null) pet.setSpecies(petRequest.getSpecies());
        if (petRequest.getBreed() != null) pet.setBreed(petRequest.getBreed());
        if (petRequest.getBirthDate() != null) pet.setBirthDate(petRequest.getBirthDate());
        if (petRequest.getPhoto() != null) pet.setPhoto(petRequest.getPhoto());

        petRepository.save(pet);
    }

    @Transactional
    public void removePet(String username, Long petId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (!user.getPets().contains(pet)) {
            throw new InvalidOperationException("Pet does not belong to this user");
        }

        user.getPets().remove(pet);
        petRepository.delete(pet);
    }
}