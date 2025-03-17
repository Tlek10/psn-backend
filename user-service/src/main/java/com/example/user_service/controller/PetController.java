package com.example.user_service.controller;

import com.example.user_service.dto.PetDTO;
import com.example.user_service.dto.PetRequest;
import com.example.user_service.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<String> addPet(@RequestBody PetRequest petRequest, Authentication authentication) {
        String username = authentication.getName();
        petService.addPet(username, petRequest);
        return ResponseEntity.ok("Pet added!");
    }

    @GetMapping
    public ResponseEntity<List<PetDTO>> getUserPets(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(petService.getUserPets(username));
    }

    @PutMapping("/{petId}")
    public ResponseEntity<String> updatePet(
            @PathVariable Long petId,
            @RequestBody PetRequest petRequest,
            Authentication authentication) {
        String username = authentication.getName();
        petService.updatePet(username, petId, petRequest);
        return ResponseEntity.ok("Pet updated!");
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<String> removePet(@PathVariable Long petId, Authentication authentication) {
        String username = authentication.getName();
        petService.removePet(username, petId);
        return ResponseEntity.ok("Pet removed!");
    }
}