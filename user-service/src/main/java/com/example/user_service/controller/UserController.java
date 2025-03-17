package com.example.user_service.controller;

import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.dto.UserUpdateDTO;
import com.example.user_service.model.User;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok("User created in User Service!");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserProfile(username));
    }
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(@RequestBody UserUpdateDTO dto, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateUser(username, dto));
    }
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserProfile(username));
    }
}