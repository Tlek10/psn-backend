package com.example.event_service.service;

import com.example.event_service.dto.EventDTO;
import com.example.event_service.dto.EventRequest;
import com.example.event_service.model.Event;
import com.example.event_service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public void createEvent(String creatorUsername, EventRequest request) {
        for (String participant : request.getParticipants()) {
            validateUser(participant);
        }

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDate(request.getDate());
        event.setDescription(request.getDescription());
        event.setCreator(creatorUsername);
        event.getParticipants().add(creatorUsername);
        event.getParticipants().addAll(request.getParticipants());

        eventRepository.save(event);
    }

    public List<EventDTO> getUserEvents(String username) {
        List<Event> events = eventRepository.findByCreatorOrParticipantsContaining(username, username);
        return events.stream().map(EventDTO::new).collect(Collectors.toList());
    }

    public List<EventDTO> getAllEvents(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findAll(pageRequest);
        return eventPage.stream().map(EventDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public void inviteUser(String creatorUsername, Long eventId, String inviteeUsername) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!event.getCreator().equals(creatorUsername)) {
            throw new IllegalArgumentException("Only the creator can invite users");
        }

        validateUser(inviteeUsername);
        if (!event.getParticipants().contains(inviteeUsername)) {
            event.getParticipants().add(inviteeUsername);
            eventRepository.save(event);
        }
    }

    @Transactional
    public void deleteEvent(String username, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!event.getCreator().equals(username)) {
            throw new IllegalArgumentException("Only the creator can delete the event");
        }

        eventRepository.delete(event);
    }

    private void validateUser(String username) {
        String userServiceUrl = "http://localhost:8082/users/" + username;
        try {
            restTemplate.getForObject(userServiceUrl, Object.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }
}