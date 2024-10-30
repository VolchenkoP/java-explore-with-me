package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.categories.model.Category;
import ru.practicum.events.dto.EventRequest;
import ru.practicum.events.dto.EventResponse;
import ru.practicum.events.dto.EventResponseShort;
import ru.practicum.events.dto.EventUpdated;
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
    @Mapping(target = "initiator", source = "event.initiator.id")
    EventRequest toRequest(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventResponseShort toResponseShort(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventResponse toResponse(Event event);

    default Event updatingEvent(Event event, EventUpdated eventUpdated, Category category) {

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

            if (eventUpdated.getStateAction().equals(String.valueOf(EventStateAction.SEND_TO_REVIEW_EVENT))) {
                event.setState(String.valueOf(EventStates.PENDING));
            }

        }
        return event;
    }
}
