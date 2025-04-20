package com.example.gatewayservice.filter;

import com.example.gatewayservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Извлекаем токен из заголовка Authorization
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            try {
                // Проверяем токен
                if (!jwtUtil.validateToken(token)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                // Извлекаем username из токена
                String username = jwtUtil.extractUsername(token);

                // Добавляем username в заголовок X-Username
                exchange.getRequest().mutate()
                        .header("X-Username", username)
                        .build();
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Продолжаем цепочку фильтров
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Пустой класс для конфигурации, если нужно
    }
}