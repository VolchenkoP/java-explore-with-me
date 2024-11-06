package ru.practicum.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.constants.Constants;
import ru.practicum.events.dto.EventRespFull;
import ru.practicum.events.dto.EventUpdate;
import ru.practicum.events.services.EventsServiceAdmin;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {

    private final EventsServiceAdmin eventService;

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventRespFull adminsUpdate(@Valid @RequestBody EventUpdate eventUpdate,
                                      @PathVariable("eventId") long eventId) {
        log.info("Обновление администратором события с id: {} и запросом: {}", eventId, eventUpdate);
        return eventService.adminsUpdate(eventUpdate, eventId);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventRespFull> getEventsByConditionalsForAdmin(
            @RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<String> states,
            @RequestParam(value = "categories", required = false) List<Integer> categories,
            @RequestParam(value = "rangeStart", required = false) @DateTimeFormat(
                    pattern = Constants.DATA_PATTERN) LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false) @DateTimeFormat(
                    pattern = Constants.DATA_PATTERN) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", defaultValue = "10") int size) {

        log.info("Поиск администратором события по параметрам users: {}, states: {}," +
                        "categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}", users, states,
                categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsByConditionalsForAdmin(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);
    }
}
