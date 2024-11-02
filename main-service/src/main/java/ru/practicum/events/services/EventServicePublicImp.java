package ru.practicum.events.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.common.config.ConnectToStatServer;
import ru.practicum.common.constants.Constants;
import ru.practicum.common.utilites.Utilities;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.model.EventStates;
import ru.practicum.events.dto.EventRespFull;
import ru.practicum.events.dto.EventRespShort;
import ru.practicum.events.model.Event;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.dto.EventIdByRequestsCount;
import ru.practicum.statisticsClient.StatisticClient;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServicePublicImp implements EventsServicePublic {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatisticClient statisticClient;
    private final EventMapper eventMapper;

    @Override
    public Collection<EventRespShort> searchEvents(String text, List<Integer> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   boolean onlyAvailable, String sort, int from, int size) {
        validateDates(rangeStart, rangeEnd);
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        if (text == null) {
            text = "";
        }
        if (categories == null) {
            categories = List.of();
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = Constants.defaultEndTime;
        }

        List<EventRespShort> events = eventRepository
                .searchEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable)
                .stream()
                .map(eventMapper::mapToEventRespShort)
                .toList();

        List<Long> eventsIds = events.stream()
                .map(EventRespShort::getId)
                .toList();

        Map<Long, Long> confirmedRequestsByEvents = requestRepository
                .countByEventIdInAndStatusGroupByEvent(eventsIds, String.valueOf(RequestStatus.CONFIRMED))
                .stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));

        List<Long> views = ConnectToStatServer.getViews(Constants.defaultStartTime,
                Constants.defaultEndTime, ConnectToStatServer.prepareUris(eventsIds),
                true, statisticClient);

        List<? extends EventRespShort> eventsForResp =
                Utilities.addViewsAndConfirmedRequests(events, confirmedRequestsByEvents, views);

        return Utilities.checkTypes(eventsForResp, EventRespShort.class);
    }

    @Override
    public EventRespFull getEvent(long eventId, String path) {
        Event event = eventRepository.findByIdAndState(eventId, String.valueOf(EventStates.PUBLISHED))
                .orElseThrow(() -> {
                    log.warn("Поиск неизвестного события");
                    return new NotFoundException("Событие с id = " + eventId + " не найдено");
                });

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId,
                String.valueOf(RequestStatus.CONFIRMED));

        EventRespFull eventFull = eventMapper.mapToEventRespFull(event);
        eventFull.setConfirmedRequests(confirmedRequests);
        List<Long> views = ConnectToStatServer.getViews(Constants.defaultStartTime,
                Constants.defaultEndTime, path,
                true, statisticClient);
        if (views.isEmpty()) {
            eventFull.setViews(0L);
        }
        eventFull.setViews(views.get(0));
        return eventFull;
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return;
        }
        if (start.isAfter(end)) {
            log.warn("Отклонено, начало не может быть позже окончания, начало: {}, окончание: {}", start, end);
            throw new ValidationException("Событие не опубликовано");
        }
    }
}
