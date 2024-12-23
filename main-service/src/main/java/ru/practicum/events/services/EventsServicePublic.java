package ru.practicum.events.services;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.events.dto.EventRespFull;
import ru.practicum.events.dto.EventRespShort;

import java.util.Collection;
import java.util.List;

public interface EventsServicePublic {

    Collection<EventRespShort> searchEvents(String text, List<Integer> categories, Boolean paid,
                                            String rangeStart, String rangeEnd, boolean onlyAvailable,
                                            String sort, int from, int size, HttpServletRequest httpServletRequest);

    EventRespFull getEvent(long eventId, HttpServletRequest request);

}
