package com.example.gatewayservice.controller;

import com.example.gatewayservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtUtil jwtUtil;

    @GetMapping("/generate-token")
    public String generateToken(@RequestParam("username") String username) {
        return jwtUtil.generateToken(username);
    }
}