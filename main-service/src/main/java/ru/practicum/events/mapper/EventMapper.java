package ru.practicum.events.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.categories.model.Category;
import ru.practicum.events.dto.EventRequest;
import ru.practicum.events.dto.EventResponse;
import ru.practicum.events.dto.EventResponseShort;
import ru.practicum.events.dto.EventUpdate;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStateAction;
import ru.practicum.events.model.EventStates;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static Event toEntity(EventRequest eventRequest) {
        return Event
                .builder()
                .id(eventRequest.getId())
                .annotation(eventRequest.getAnnotation())
                .description(eventRequest.getDescription())
                .eventDate(eventRequest.getEventDate())
                .location(eventRequest.getLocation())
                .paid(eventRequest.getPaid())
                .participantLimit(eventRequest.getParticipantLimit())
                .requestModeration(eventRequest.getRequestModeration())
                .title(eventRequest.getTitle())
                .build();
    }

    public static EventRequest toRequest(Event event) {
        return EventRequest
                .builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory().getId())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .title(event.getTitle())
                .initiator(event.getId())
                .requestModeration(event.getRequestModeration())
                .createdOn(event.getCreatedOn())
                .state(event.getState())
                .build();
    }

    public static EventResponseShort toResponseShort(Event event) {
        return EventResponseShort
                .builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();

    }

    public static EventResponse toResponse(Event event) {
        return EventResponse
                .builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .createdOn(event.getCreatedOn())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .title(event.getTitle())
                .initiator(event.getInitiator())
                .requestModeration(event.getRequestModeration())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .build();
    }

    public static Event updatingEvent(Event updatingEvent, EventUpdate eventUpdate, Category category) {

        if (eventUpdate.getId() != null) {
            updatingEvent.setId(eventUpdate.getId());
        }

        if (eventUpdate.getAnnotation() != null) {
            updatingEvent.setAnnotation(eventUpdate.getAnnotation());
        }

        if (eventUpdate.getCategory() != null) {
            updatingEvent.setCategory(category);
        }

        if (eventUpdate.getDescription() != null) {
            updatingEvent.setDescription(eventUpdate.getDescription());
        }

        if (eventUpdate.getLocation() != null) {
            updatingEvent.setLocation(eventUpdate.getLocation());
        }

        if (eventUpdate.getPaid() != null) {
            updatingEvent.setPaid(eventUpdate.getPaid());
        }

        if (eventUpdate.getParticipantLimit() != null) {
            updatingEvent.setParticipantLimit(eventUpdate.getParticipantLimit());
        }

        if (eventUpdate.getRequestModeration() != null) {
            updatingEvent.setRequestModeration(eventUpdate.getRequestModeration());
        }

        if (eventUpdate.getTitle() != null) {
            updatingEvent.setTitle(eventUpdate.getTitle());
        }

        if (eventUpdate.getCreatedOn() != null) {
            updatingEvent.setCreatedOn(eventUpdate.getCreatedOn());
        }

        if (eventUpdate.getStateAction() != null) {
            if (eventUpdate.getStateAction().equals(String.valueOf(EventStateAction.PUBLISH_EVENT))) {
                updatingEvent.setState(String.valueOf(EventStates.PUBLISHED));
                updatingEvent.setPublishedOn(LocalDateTime.now());
            }

            if ((eventUpdate.getStateAction().equals(String.valueOf(EventStateAction.REJECT_EVENT)))
                    || (eventUpdate.getStateAction().equals(String.valueOf(EventStateAction.CANCEL_REVIEW)))) {
                updatingEvent.setState(String.valueOf(EventStates.CANCELED));
            }

            if (eventUpdate.getStateAction().equals(String.valueOf(EventStateAction.SEND_TO_REVIEW))) {
                updatingEvent.setState(String.valueOf(EventStates.PENDING));
            }

        }
        return updatingEvent;
    }
}