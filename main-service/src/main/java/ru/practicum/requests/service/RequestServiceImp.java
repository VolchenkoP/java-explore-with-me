package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStates;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.model.Requests;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImp implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    private static void sortByRequesterIdAndEventId(List<Requests> list) {
        list.sort((Requests req1, Requests req2) -> {
            if (req1.getRequester().getId() > req2.getRequester().getId()) {
                return 1;
            } else if (req1.getRequester().getId().equals(req2.getRequester().getId())) {
                return req1.getEvent().getId().compareTo(req2.getEvent().getId());
            } else {
                return -1;
            }
        });
    }

    private static void isThereDuplicate(List<Requests> list, long requesterTargetId, long eventTargetId) {
        sortByRequesterIdAndEventId(list);

        int low = 0;
        int high = list.size() - 1;
        int mid;

        while (low <= high) {
            mid = (low + high) / 2;

            if (list.get(mid).getRequester().getId().equals(requesterTargetId)) {
                if (list.get(mid).getEvent().getId().equals(eventTargetId)) {
                    log.warn("Запрос с id: {} направлен повторно", requesterTargetId);
                    throw new ConflictException("Запрос с id: " + requesterTargetId + " дубликат");
                } else {
                    if (list.get(mid).getEvent().getId() < eventTargetId) {
                        low = mid + 1;
                    } else {
                        high = mid - 1;
                    }
                }

            } else if (list.get(mid).getRequester().getId() < requesterTargetId) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
    }

    @Override
    @Transactional
    public RequestDto addRequest(long eventId, long userId) {
        Event event = validateEvent(eventId);
        User user = validateUser(userId);
        checkAbilityToAddRequest(event, userId, eventId);
        Requests addingRequest = Requests
                .builder()
                .created(LocalDateTime.now())
                .build();

        addingRequest.setEvent(event);
        addingRequest.setRequester(user);

        addingRequest.setStatus(String.valueOf(RequestStatus.PENDING));

        if (!event.getRequestModeration()) {
            addingRequest.setStatus(String.valueOf(RequestStatus.CONFIRMED));
        }

        if (event.getParticipantLimit() == 0) {
            addingRequest.setStatus(String.valueOf(RequestStatus.CONFIRMED));
        }

        return requestMapper.mapToRequestDto(requestRepository.save(addingRequest));
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(long requestId, long userId) {
        Requests updatingRequest = validateRequest(requestId);
        updatingRequest.setStatus(String.valueOf(RequestStatus.CANCELED));
        return requestMapper.mapToRequestDto(updatingRequest);
    }

    @Override
    public Collection<RequestDto> getMyRequests(long userId) {
        return requestRepository
                .findByRequesterId(userId)
                .stream()
                .map(requestMapper::mapToRequestDto)
                .collect(Collectors.toSet());
    }

    private void checkAbilityToAddRequest(Event event, long requesterId, long eventId) {
        List<Requests> requests = requestRepository.findByRequesterId(requesterId);
        isThereDuplicate(requests, requesterId, eventId);
        validateEventStatesAndRequesterId(event, requesterId);
        checkParticipantsLimit(event);
    }

    private void validateEventStatesAndRequesterId(Event event, Long requesterId) {
        if (event.getState().equals(String.valueOf(EventStates.PENDING))
                || event.getState().equals(String.valueOf(EventStates.CANCELED))) {
            log.warn("Событие с id: {} не опубликовано. Запрос отклонен", event.getId());
            throw new ConflictException("Событие с id = " + event.getId() + " не опубликовано");
        }

        if (event.getInitiator().getId().equals(requesterId)) {
            log.warn("Запрос к своему событию");
            throw new ConflictException("Невозможно сделать запрос к событию");
        }
    }

    private void checkParticipantsLimit(Event event) {
        long requestAmountForEvent = requestRepository.countByEventIdAndStatus(event.getId(),
                String.valueOf(RequestStatus.CONFIRMED));

        if ((event.getParticipantLimit() != 0)
                && (event.getParticipantLimit() < (requestAmountForEvent + 1))) {
            log.warn("Невозможно добавить запрос. Лимит участников {} меньше, чем количество запросов: {}",
                    event.getParticipantLimit(), (requestAmountForEvent + 1));
            throw new ConflictException("Превышено количество запросов");
        }
    }

    private Requests validateRequest(long requestId) {

        return requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Поиск несуществующего запроса по id: {}", requestId);
                    return new NotFoundException("Запрос с id = " + requestId + " не найден");
                });
    }

    private Event validateEvent(long eventId) {

        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Поиск несуществующего события по id: {}", eventId);
                    return new NotFoundException("Событие с id: " + eventId + " не найдено");
                });
    }

    private User validateUser(long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Поиск несуществующего пользователя по id: {}", userId);
                    return new NotFoundException("Пользователь с id = " + userId + " не найден");
                });
    }
}


