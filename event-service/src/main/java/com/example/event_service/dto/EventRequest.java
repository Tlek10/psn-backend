package com.example.event_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequest {
    private String title;
    private LocalDateTime date;
    private String description;
    private List<String> participants; // usernames
}