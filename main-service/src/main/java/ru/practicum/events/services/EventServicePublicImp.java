package ru.practicum.events.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatisticDto;
import ru.practicum.common.config.ConnectToStatServer;
import ru.practicum.common.constants.Constants;
import ru.practicum.common.utilites.Utilities;
import ru.practicum.events.dto.EventRespFull;
import ru.practicum.events.dto.EventRespShort;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStates;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dto.EventIdByRequestsCount;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.statisticsClient.StatisticClient;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
                                                   String rangeStart, String rangeEnd,
                                                   boolean onlyAvailable, String sort, int from, int size,
                                                   HttpServletRequest httpServletRequest) {

        String ip = httpServletRequest.getRemoteAddr();
        String path = httpServletRequest.getRequestURI();

        log.info("Поиск события по параметрам: text: {}, categories: {}, paid: {}," +
                        "rangeStart: {}, rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}", text,
                categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Пользователем с параметрами ip: {}, path: {}", ip, path);

        StatisticDto statisticDto = prepareStatisticDto("ewm-main-service", path, ip);
        ResponseEntity<Object> response = statisticClient.addStat(statisticDto);

        validateResponses(response);

        log.info("В сервер статистики ушел следующий запрос: statisticDto: {}",
                statisticDto);

        LocalDateTime start = convertToLocalDataTime(decode(rangeStart));
        LocalDateTime end = convertToLocalDataTime(decode(rangeEnd));

        validateDates(start, end);
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        if (text == null) {
            text = "";
        }
        if (categories == null) {
            categories = List.of();
        }
        if (start == null) {
            start = LocalDateTime.now();
        }
        if (end == null) {
            end = Constants.DEFAULT_END_TIME;
        }

        List<EventRespShort> events = eventRepository
                .searchEvents(text, categories, paid, start, end, onlyAvailable, pageable)
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

        List<Long> views = ConnectToStatServer.getViews(Constants.DEFAULT_START_TIME,
                Constants.DEFAULT_END_TIME, ConnectToStatServer.prepareUris(eventsIds),
                true, statisticClient);

        List<? extends EventRespShort> eventsForResp =
                Utilities.addViewsAndConfirmedRequests(events, confirmedRequestsByEvents, views);

        return Utilities.checkTypes(eventsForResp, EventRespShort.class);
    }

    @Override
    public EventRespFull getEvent(long eventId, HttpServletRequest httpServletRequest) {

        String ip = httpServletRequest.getRemoteAddr();
        String path = httpServletRequest.getRequestURI();

        StatisticDto statisticDto = prepareStatisticDto("ewm-main-service", path, ip);
        ResponseEntity<Object> response = statisticClient.addStat(statisticDto);

        validateResponses(response);

        log.info("Следующий запрос ушел в сервис статистики: statisticDto: {}", statisticDto);

        Event event = eventRepository.findByIdAndState(eventId, String.valueOf(EventStates.PUBLISHED))
                .orElseThrow(() -> {
                    log.warn("Поиск неизвестного события");
                    return new NotFoundException("Событие с id = " + eventId + " не найдено");
                });

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId,
                String.valueOf(RequestStatus.CONFIRMED));

        EventRespFull eventFull = eventMapper.mapToEventRespFull(event);
        eventFull.setConfirmedRequests(confirmedRequests);
        List<Long> views = ConnectToStatServer.getViews(Constants.DEFAULT_START_TIME,
                Constants.DEFAULT_END_TIME, path,
                true, statisticClient);
        if (views.isEmpty()) {
            eventFull.setViews(0L);
        }
        eventFull.setViews(views.getFirst());
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

    private StatisticDto prepareStatisticDto(String app, String uri, String ip) {
        return StatisticDto
                .builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void validateResponses(ResponseEntity<?> response) {
        if (response.getStatusCode().is4xxClientError()) {
            log.error("Ответ пришел с параметрами: Status code: {}, responseBody: {}", response.getStatusCode(),
                    response.getBody());
        }

        if (response.getStatusCode().is5xxServerError()) {
            log.error("Ответ пришел с параметрами: Status code: {}, responseBody: {}", response.getStatusCode(),
                    response.getBody());
        }
    }

    private String decode(String parameter) {
        if (parameter == null) {
            return null;
        }
        return URLDecoder.decode(parameter, StandardCharsets.UTF_8);
    }

    private LocalDateTime convertToLocalDataTime(String date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.parse(date, Constants.DATE_FORMATTER);
    }
}
