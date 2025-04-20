package com.example.event_service.dto;

import com.example.event_service.model.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private Long id;
    private String title;
    private LocalDateTime date;
    private String description;
    private String creator;
    private List<String> participants;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.date = event.getDate();
        this.description = event.getDescription();
        this.creator = event.getCreator();
        this.participants = event.getParticipants();
    }


}