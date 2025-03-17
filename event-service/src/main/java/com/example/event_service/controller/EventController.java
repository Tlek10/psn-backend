package com.example.event_service.controller;

import com.example.event_service.dto.EventDTO;
import com.example.event_service.dto.EventRequest;
import com.example.event_service.dto.InviteRequest;
import com.example.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<String> createEvent(@RequestBody EventRequest request, Authentication authentication) {
        String username = authentication.getName();
        eventService.createEvent(username, request);
        return ResponseEntity.ok("Event created!");
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getEvents(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(eventService.getUserEvents(username));
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(eventService.getAllEvents(page, size));
    }

    @PostMapping("/{eventId}/invite")
    public ResponseEntity<String> inviteUser(@PathVariable Long eventId, @RequestBody InviteRequest request, Authentication authentication) {
        String username = authentication.getName();
        eventService.inviteUser(username, eventId, request.getUsername());
        return ResponseEntity.ok("User invited!");
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId, Authentication authentication) {
        String username = authentication.getName();
        eventService.deleteEvent(username, eventId);
        return ResponseEntity.ok("Event deleted!");
    }
}