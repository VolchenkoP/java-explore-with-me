package ru.practicum.events.service.publicService;

import ru.practicum.events.dto.EventResponse;
import ru.practicum.events.dto.EventResponseShort;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventPublicService {
    Collection<EventResponseShort> searchEvents(String text, List<Integer> categories, Boolean paid,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                                String sort, int from, int size);

    EventResponse getEvent(long eventId, String path);
}
