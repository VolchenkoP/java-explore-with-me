package ru.practicum.subscriptions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.config.ConnectToStatServer;
import ru.practicum.common.constants.Constants;
import ru.practicum.common.utilites.Utilities;
import ru.practicum.events.dto.EventRespShort;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.EventStates;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.EventIdByRequestsCount;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.statisticsClient.StatisticClient;
import ru.practicum.subscriptions.dto.SubscriptionDto;
import ru.practicum.subscriptions.mapper.SubscriptionMapper;
import ru.practicum.subscriptions.model.Subscription;
import ru.practicum.subscriptions.repository.SubscriptionRepository;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubscriptionServiceImp implements SubscriptionsService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatisticClient statisticClient;
    private final SubscriptionMapper subscriptionMapper;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public SubscriptionDto subscribeToUser(long userId, long followerId) {
        User user = validateAndGetUser(userId);
        User follower = validateAndGetUser(followerId);
        validateSubscription(userId, followerId);
        Subscription addingSubscription = new Subscription();
        addingSubscription.setUser(user);
        addingSubscription.setFollower(follower);
        return subscriptionMapper.mapToSubscriptionDto(subscriptionRepository.save(addingSubscription));
    }

    @Override
    @Transactional
    public void cancelSubscribe(long userId, long followerId) {
        validateAndGetUser(userId);
        validateAndGetUser(followerId);
        Subscription subscription = validateAndGetSubscription(userId, followerId);
        subscriptionRepository.deleteById(subscription.getSubscriptionId());
    }

    @Override
    public List<UserDto> getUsersIFollow(long userId) {
        validateAndGetUser(userId);
        return subscriptionRepository
                .findByFollowerId(userId)
                .stream()
                .map((userProjection) ->
                        new User(userProjection.getId(), userProjection.getEmail(), userProjection.getName()))
                .map(userMapper::mapToUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getMyFollowers(long userId) {
        validateAndGetUser(userId);
        return subscriptionRepository
                .findByUserId(userId)
                .stream()
                .map((userProjection) ->
                        new User(userProjection.getId(), userProjection.getEmail(), userProjection.getName()))
                .map(userMapper::mapToUserDto)
                .toList();
    }

    @Override
    public List<EventRespShort> getUsersEvents(long userId, int from, int size) {
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);
        List<EventRespShort> events = eventRepository
                .findByInitiatorIdAndState(userId, String.valueOf(EventStates.PUBLISHED), pageable)
                .stream()
                .map(eventMapper::mapToEventRespShort)
                .toList();

        List<Long> eventsIds = events
                .stream()
                .map(EventRespShort::getId)
                .toList();

        Map<Long, Long> confirmedRequestsByEvents = requestRepository
                .countByEventIdInAndStatusGroupByEvent(eventsIds, String.valueOf(RequestStatus.CONFIRMED))
                .stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));

        List<Long> views = ConnectToStatServer.getViews(Constants.DEFAULT_START_TIME, Constants.DEFAULT_END_TIME,
                ConnectToStatServer.prepareUris(eventsIds), true, statisticClient);

        List<? extends EventRespShort> eventsForResp =
                Utilities.addViewsAndConfirmedRequests(events, confirmedRequestsByEvents, views);

        return Utilities.checkTypes(eventsForResp, EventRespShort.class);
    }

    private void validateSubscription(long userId, long followerId) {
        if (userId == followerId) {
            log.warn("Попытка пользователя с id: {} подписаться на себя)", userId);
            throw new ConflictException("Пользователь с id: " + userId + " пытается подписаться на себя (followerId: "
                    + followerId + ")");
        }

        subscriptionRepository.findByUserIdAndFollowerId(userId, followerId)
                .map(subscription -> {
                    log.warn("Пользователь с id: {} уже подписан на пользователя с id: {}", followerId, userId);
                    throw new ConflictException("Пользователь с id: " + followerId +
                            " уже подписан на пользователя с id: " + userId);
                })
                .orElseThrow();
    }

    private User validateAndGetUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Поиск несуществующего пользователя с id: {}", userId);
            return new NotFoundException("Пользователь с id = " + userId + " не существует");
        });
    }

    private Subscription validateAndGetSubscription(long userId, long followerId) {
        return subscriptionRepository.findByUserIdAndFollowerId(userId, followerId).orElseThrow(() -> {
            log.warn("Пользователь с id: {} не подписан на пользователя с id: {}", followerId, userId);
            return new NotFoundException("Пользователь с id: " + followerId +
                    " не подписан на пользователя с id: " + userId);
        });
    }
}
