package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestsController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@RequestParam("eventId") long eventId,
                                 @PathVariable("userId") long userId) {
        log.info("RequestsController, addRequest. EventId: {}, userId: {}", eventId, userId);
        return requestService.addRequest(eventId, userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable("requestId") long requestId,
                                    @PathVariable("userId") long userId) {
        log.info("RequestsController, cancelRequest, RequestId: {}, userId: {}", requestId, userId);
        return requestService.cancelRequest(requestId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestDto> getMyRequests(@PathVariable("userId") long userId) {
        log.info("RequestsController, getMyRequests. UserId: {}", userId);
        return requestService.getMyRequests(userId);
    }
}
