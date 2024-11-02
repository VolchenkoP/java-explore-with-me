package ru.practicum.events.service.privateService;

import ru.practicum.events.dto.EventRequest;
import ru.practicum.events.dto.EventResponse;
import ru.practicum.events.dto.EventResponseShort;
import ru.practicum.events.dto.EventUpdate;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestResponse;
import ru.practicum.requests.dto.RequestsForConfirmation;

import java.util.Collection;

public interface EventPrivateService {

    EventRequest createEvent(EventRequest eventRequest, long userId);

    Collection<EventResponseShort> getUsersEvents(long userId, int from, int size);

    EventResponse getUsersFullEventById(long userId, long eventId, String path);

    EventRequest updateUsersEvent(long userId, long eventId, EventUpdate eventUpdate);

    Collection<RequestDto> getRequestsByEventId(long eventId, long userId);

    RequestResponse approveRequests(RequestsForConfirmation requestsForConfirmation,
                                    long userId,
                                    long eventId);
}
