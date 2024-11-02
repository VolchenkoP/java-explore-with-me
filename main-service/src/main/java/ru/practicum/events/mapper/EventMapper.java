package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.categories.model.Category;
import ru.practicum.events.model.EventStates;
import ru.practicum.events.dto.EventRequest;
import ru.practicum.events.dto.EventRespFull;
import ru.practicum.events.dto.EventRespShort;
import ru.practicum.events.dto.EventUpdate;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStateAction;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    Event mapToEvent(EventRequest request);

    @Mapping(target = "category", source = "event.category.id")
    @Mapping(target = "initiator", source = "event.initiator.id")
    EventRequest mapToEventRequest(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventRespShort mapToEventRespShort(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventRespFull mapToEventRespFull(Event event);

    default Event updateEvent(Event event, EventUpdate eventUpdated, Category category) {

        if (eventUpdated.getId() != null) {
            event.setId(eventUpdated.getId());
        }

        if (eventUpdated.getAnnotation() != null) {
            event.setAnnotation(eventUpdated.getAnnotation());
        }

        if (eventUpdated.getCategory() != null) {
            event.setCategory(category);
        }

        if (eventUpdated.getDescription() != null) {
            event.setDescription(eventUpdated.getDescription());
        }

        if (eventUpdated.getLocation() != null) {
            event.setLocation(eventUpdated.getLocation());
        }

        if (eventUpdated.getPaid() != null) {
            event.setPaid(eventUpdated.getPaid());
        }

        if (eventUpdated.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdated.getParticipantLimit());
        }

        if (eventUpdated.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdated.getRequestModeration());
        }

        if (eventUpdated.getTitle() != null) {
            event.setTitle(eventUpdated.getTitle());
        }

        if (eventUpdated.getCreatedOn() != null) {
            event.setCreatedOn(eventUpdated.getCreatedOn());
        }

        if (eventUpdated.getStateAction() != null) {
            if (eventUpdated.getStateAction().equals(String.valueOf(EventStateAction.PUBLISH_EVENT))) {
                event.setState(String.valueOf(EventStates.PUBLISHED));
                event.setPublishedOn(LocalDateTime.now());
            }

            if ((eventUpdated.getStateAction().equals(String.valueOf(EventStateAction.REJECT_EVENT)))
                    || (eventUpdated.getStateAction().equals(String.valueOf(EventStateAction.CANCEL_REVIEW)))) {
                event.setState(String.valueOf(EventStates.CANCELED));
            }

            if (eventUpdated.getStateAction().equals(String.valueOf(EventStateAction.SEND_TO_REVIEW))) {
                event.setState(String.valueOf(EventStates.PENDING));
            }

        }
        return event;
    }
}
