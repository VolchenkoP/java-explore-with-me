package ru.practicum.subscriptions.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.events.dto.EventRespShort;
import ru.practicum.subscriptions.dto.SubscriptionDto;
import ru.practicum.subscriptions.service.SubscriptionsService;
import ru.practicum.users.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionsController {

    private final SubscriptionsService subscriptionsService;

    @PostMapping("/{userId}/subscribe/{followerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribeToUser(@PathVariable(name = "userId") long userId,
                                           @PathVariable(name = "followerId") long followerId) {
        log.info("Добавление подписки на пользователя с id: {}, пользователем с id: {}", userId, followerId);
        return subscriptionsService.subscribeToUser(userId, followerId);

    }

    @DeleteMapping("/{userId}/subscribe/{followerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelSubscribe(@PathVariable(name = "userId") long userId,
                                @PathVariable(name = "followerId") long followerId) {
        log.info("Отмена подписки на пользователя с id: {}, пользователем с id: {}", userId, followerId);
        subscriptionsService.cancelSubscribe(userId, followerId);

    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsersIFollow(@PathVariable(name = "userId") long userId) {
        log.info("Поиск подписчиков пользователя с id: {}", userId);
        return subscriptionsService.getUsersIFollow(userId);
    }

    @GetMapping("/{userId}/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getMyFollowers(@PathVariable(name = "userId") long userId) {
        log.info("Поиск пользователем с id: {} подписчиков", userId);
        return subscriptionsService.getMyFollowers(userId);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventRespShort> getUsersEvents(@PathVariable(name = "userId") long userId,
                                               @RequestParam(value = "from", defaultValue = "0") int from,
                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Поиск событий пользователя с id: {} и параметрами from: {}, size: {}", userId, from, size);
        return subscriptionsService.getUsersEvents(userId, from, size);
    }
}
