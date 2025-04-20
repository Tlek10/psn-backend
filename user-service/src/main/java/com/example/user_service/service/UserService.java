package com.example.user_service.service;

import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.dto.UserUpdateDTO;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        userRepository.save(user);
    }

    public UserProfileDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToProfileDTO(user);
    }

    public User updateUser(String username, UserUpdateDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        user.setEmail(dto.getEmail());
        return userRepository.save(user);
    }

    private UserProfileDTO mapToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}