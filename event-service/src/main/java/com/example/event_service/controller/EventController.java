package com.example.event_service.controller;

import com.example.event_service.dto.EventDTO;
import com.example.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public EventDTO createEvent(@RequestBody EventDTO eventDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return eventService.createEvent(eventDTO, username);
    }

    @GetMapping
    public List<EventDTO> getAllEvents() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return eventService.getAllEvents(username);
    }

    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return eventService.getEventById(id, username);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        eventService.deleteEvent(id, username);
    }
}