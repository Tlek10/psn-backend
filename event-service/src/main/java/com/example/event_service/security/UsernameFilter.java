package com.example.event_service.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class UsernameFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Извлекаем username из заголовка X-Username
        String username = request.getHeader("X-Username");
        if (username == null || username.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // Создаём объект UserDetails на основе username
            UserDetails userDetails = new User(username, "", Collections.emptyList());
            // Устанавливаем пользователя в контекст безопасности
            SecurityContextHolder.getContext().setAuthentication(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    )
            );
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}