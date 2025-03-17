package com.example.auth_service.controller;

import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RestTemplate restTemplate; // Добавляем RestTemplate для вызова User Service

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        try {
            // Исправляем URL на имя сервиса в Docker-сети
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://user-service:8082/users", request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Failed to sync with User Service: " + response.getStatusCode());
            } else {
                System.out.println("User synced with User Service: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Failed to register user in User Service: " + e.getMessage());
        }
        return ResponseEntity.ok("User registered!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authService.authenticate(request);
        return ResponseEntity.ok(token);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}