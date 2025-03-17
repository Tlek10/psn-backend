package com.example.user_service.service;

import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.dto.UserUpdateDTO;
import com.example.user_service.exception.custom.DuplicateEntityException;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void createUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateEntityException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEntityException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setBio(request.getBio());
        user.setProfilePicture(request.getProfilePicture());
        userRepository.save(user);
    }

    public UserProfileDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new UserProfileDTO(user);
    }

    @Transactional
    public User updateUser(String username, UserUpdateDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new DuplicateEntityException("Email already used");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
                throw new DuplicateEntityException("Username already used");
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getProfilePicture() != null) user.setProfilePicture(dto.getProfilePicture());

        return userRepository.save(user);
    }
}