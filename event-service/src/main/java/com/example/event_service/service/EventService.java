package com.example.event_service.service;

import com.example.event_service.dto.EventDTO;
import com.example.event_service.model.Event;
import com.example.event_service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventDTO createEvent(EventDTO eventDTO, String username) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setCreator(username);
        event = eventRepository.save(event);
        return mapToDTO(event);
    }

    public List<EventDTO> getAllEvents(String username) {
        return eventRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public EventDTO getEventById(Long id, String username) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        return mapToDTO(event);
    }

    @Transactional
    public void deleteEvent(Long id, String username) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        if (!event.getCreator().equals(username)) {
            throw new RuntimeException("Only the creator can delete the event");
        }
        eventRepository.deleteById(id);
    }

    private EventDTO mapToDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setTitle(event.getTitle());
        eventDTO.setDate(event.getDate());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setCreator(event.getCreator());
        eventDTO.setParticipants(event.getParticipants());
        return eventDTO;
    }
}