package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.events.dto.EventRespFull;
import ru.practicum.events.dto.EventRespShort;
import ru.practicum.events.services.EventsServicePublic;
import ru.practicum.statisticsClient.StatisticClient;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {

    private final EventsServicePublic eventService;
    private final StatisticClient statisticClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventRespShort> searchEvents(@RequestParam(value = "text", required = false) String text,
                                                   @RequestParam(value = "categories", required = false)
                                                   List<Integer> categories,
                                                   @RequestParam(value = "paid", required = false) Boolean paid,
                                                   @RequestParam(value = "rangeStart",
                                                           required = false) String rangeStart,
                                                   @RequestParam(value = "rangeEnd",
                                                           required = false) String rangeEnd,
                                                   @RequestParam(value = "onlyAvailable",
                                                           defaultValue = "false") boolean onlyAvailable,
                                                   @RequestParam(value = "sort", required = false) String sort,
                                                   @PositiveOrZero @RequestParam(value = "from",
                                                           defaultValue = "0") int from,
                                                   @Positive @RequestParam(value = "size",
                                                           defaultValue = "10") int size,
                                                   HttpServletRequest httpServletRequest) {

        log.info("Поиск события по параметрам: text: {}, categories: {}, paid: {}," +
                        "rangeStart: {}, rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}", text,
                categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return eventService.searchEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                httpServletRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventRespFull getEvent(@PathVariable("id") long eventId,
                                  HttpServletRequest httpServletRequest) {

        log.info("Поиск события с по id: {}", eventId);

        return eventService.getEvent(eventId, httpServletRequest);
    }


}
