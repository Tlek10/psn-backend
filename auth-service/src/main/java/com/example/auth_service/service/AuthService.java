package com.example.auth_service.service;

import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);
        userRepository.save(user);

        // Синхронизация с user-service через Gateway
        String userServiceUrl = "http://gateway-service:8080/users";
        try {
            String response = restTemplate.postForObject(userServiceUrl, request, String.class);
            System.out.println("User Service response: " + response);
        } catch (Exception e) {
            System.err.println("Failed to register user in User Service: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to sync with User Service: " + e.getMessage());
        }

        // Запрашиваем токен у gateway-service
        String gatewayUrl = "http://gateway-service:8080/generate-token?username=" + request.getUsername();
        try {
            return restTemplate.getForObject(gatewayUrl, String.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token: " + e.getMessage());
        }
    }

    public String authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Запрашиваем токен у gateway-service
        String gatewayUrl = "http://gateway-service:8080/generate-token?username=" + request.getUsername();
        try {
            return restTemplate.getForObject(gatewayUrl, String.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token: " + e.getMessage());
        }
    }
}