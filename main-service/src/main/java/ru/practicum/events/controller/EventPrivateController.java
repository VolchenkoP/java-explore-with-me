package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.events.dto.EventRequest;
import ru.practicum.events.dto.EventResponse;
import ru.practicum.events.dto.EventResponseShort;
import ru.practicum.events.dto.EventUpdated;
import ru.practicum.events.service.privateService.EventPrivateService;
import ru.practicum.requests.dto.RequestConfirm;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestResponse;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {

    private final EventPrivateService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventRequest createEvent(@Valid @RequestBody EventRequest eventRequest,
                                    @PathVariable(value = "userId") long userId) {
        log.info("EventPrivateController, createEvent. UserId: {}, eventRequest: {}",
                userId, eventRequest.toString());
        return eventService.createEvent(eventRequest, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventResponseShort> getUsersEvents(@PathVariable(value = "userId") long userId,
                                                         @Min(0)
                                                         @RequestParam(value = "from", defaultValue = "0") int from,
                                                         @Min(0)
                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("EventPrivateController, getUsersEvents. UserId: {}, from: {}, size: {}", userId, from, size);
        return eventService.getUsersEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse getUsersFullEventById(@PathVariable(value = "userId") long userId,
                                               @PathVariable(value = "eventId") long eventId,
                                               HttpServletRequest request) {
        log.info("EventPrivateController, getUsersFullEvent. UserId: {}, eventId: {}", userId, eventId);
        return eventService.getUsersFullEventById(userId, eventId, request.getRequestURI());
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventRequest updateUsersEvent(@PathVariable(value = "userId") long userId,
                                         @PathVariable(value = "eventId") long eventId,
                                         @Valid @RequestBody EventUpdated eventUpdate) {
        log.info("EventPrivateController, updateUsersEvent. UserId: {}, eventId: {}, eventUpdate: {}",
                userId, eventId, eventUpdate);
        return eventService.updateUsersEvent(userId, eventId, eventUpdate);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestDto> getRequestsByEvenId(@PathVariable(value = "userId") long userId,
                                                      @PathVariable(value = "eventId") long eventId) {
        log.info("EventPrivateController, getRequestsByEvenId. UserId: {}, eventId: {}", userId, eventId);
        return eventService.getRequestsByEventId(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestResponse approveRequest(@RequestBody RequestConfirm requestsForConfirmation,
                                          @PathVariable(value = "userId") long userId,
                                          @PathVariable(value = "eventId") long eventId) {
        log.info("EventPrivateController, approveRequest. UserId: {}, eventId: {}", userId, eventId);
        return eventService.approveRequests(requestsForConfirmation, userId, eventId);
    }
}