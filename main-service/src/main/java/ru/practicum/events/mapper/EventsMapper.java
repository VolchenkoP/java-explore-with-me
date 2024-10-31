package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.categories.model.Category;
import ru.practicum.events.dto.EventRequest;
import ru.practicum.events.dto.EventResponse;
import ru.practicum.events.dto.EventResponseShort;
import ru.practicum.events.dto.EventUpdate;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStateAction;
import ru.practicum.events.model.EventStates;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EventsMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    Event toEntity(EventRequest request);

    @Mapping(target = "category", source = "event.category.id")
    @Mapping(target = "initiator", source = "event.id")
    EventRequest toRequest(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventResponseShort toResponseShort(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventResponse toResponse(Event event);

    default Event updatingEvent(Event event, EventUpdate eventUpdate, Category category) {

        if (eventUpdate.getId() != null) {
            event.setId(eventUpdate.getId());
        }

        if (eventUpdate.getAnnotation() != null) {
            event.setAnnotation(eventUpdate.getAnnotation());
        }

        if (eventUpdate.getCategory() != null) {
            event.setCategory(category);
        }

        if (eventUpdate.getDescription() != null) {
            event.setDescription(eventUpdate.getDescription());
        }

        if (eventUpdate.getLocation() != null) {
            event.setLocation(eventUpdate.getLocation());
        }

        if (eventUpdate.getPaid() != null) {
            event.setPaid(eventUpdate.getPaid());
        }

        if (eventUpdate.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdate.getParticipantLimit());
        }

        if (eventUpdate.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdate.getRequestModeration());
        }

        if (eventUpdate.getTitle() != null) {
            event.setTitle(eventUpdate.getTitle());
        }

        if (eventUpdate.getCreatedOn() != null) {
            event.setCreatedOn(eventUpdate.getCreatedOn());
        }

        if (eventUpdate.getStateAction() != null) {
            if (eventUpdate.getStateAction().equals(String.valueOf(EventStateAction.PUBLISH_EVENT))) {
                event.setState(String.valueOf(EventStates.PUBLISHED));
                event.setPublishedOn(LocalDateTime.now());
            }

            if ((eventUpdate.getStateAction().equals(String.valueOf(EventStateAction.REJECT_EVENT)))
                    || (eventUpdate.getStateAction().equals(String.valueOf(EventStateAction.CANCEL_REVIEW)))) {
                event.setState(String.valueOf(EventStates.CANCELED));
            }

            if (eventUpdate.getStateAction().equals(String.valueOf(EventStateAction.SEND_TO_REVIEW))) {
                event.setState(String.valueOf(EventStates.PENDING));
            }

        }
        return event;
    }
}
