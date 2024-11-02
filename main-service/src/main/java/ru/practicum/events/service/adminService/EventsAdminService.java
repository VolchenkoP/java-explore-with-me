package ru.practicum.events.service.adminService;

import ru.practicum.events.dto.EventResponse;
import ru.practicum.events.dto.EventUpdate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventsAdminService {

    EventResponse adminsUpdate(EventUpdate eventUpdate, long eventId);

    Collection<EventResponse> getEventsByConditionalsForAdmin(List<Long> users,
                                                              List<String> states,
                                                              List<Integer> categories,
                                                              LocalDateTime rangeStart,
                                                              LocalDateTime rangeEnd,
                                                              int from,
                                                              int size);


}
