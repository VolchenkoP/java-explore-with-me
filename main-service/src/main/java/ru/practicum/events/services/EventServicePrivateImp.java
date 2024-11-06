package ru.practicum.events.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoriesRepository;
import ru.practicum.common.config.ConnectToStatServer;
import ru.practicum.common.constants.Constants;
import ru.practicum.common.utilites.Utilities;
import ru.practicum.events.dto.EventRequest;
import ru.practicum.events.dto.EventRespFull;
import ru.practicum.events.dto.EventRespShort;
import ru.practicum.events.dto.EventUpdate;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStates;
import ru.practicum.events.model.Location;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.EventIdByRequestsCount;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestResponse;
import ru.practicum.requests.dto.RequestsForConfirmation;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.model.Requests;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.statisticsClient.StatisticClient;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServicePrivateImp implements EventServicePrivate {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatisticClient statisticClient;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public EventRequest createEvent(EventRequest eventRequest, long userId) {
        if (eventRequest.getRequestModeration() == null) {
            eventRequest.setRequestModeration(true);
        }
        if (eventRequest.getPaid() == null) {
            eventRequest.setPaid(false);
        }
        if (eventRequest.getParticipantLimit() == null) {
            eventRequest.setParticipantLimit(0);
        }

        validateEventDate(eventRequest.getEventDate());
        addLocation(eventRequest.getLocation());
        Event addingEvent = eventMapper.mapToEvent(eventRequest);
        addingEvent.setInitiator(validateAndGetUser(userId));
        addingEvent.setCategory(validateAndGetCategory(eventRequest.getCategory()));
        addingEvent.setCreatedOn(LocalDateTime.now());
        addingEvent.setState(String.valueOf(EventStates.PENDING));

        Event saved = eventRepository.save(addingEvent);
        return eventMapper.mapToEventRequest(saved);
    }

    @Override
    public Collection<EventRespShort> getUsersEvents(long userId, int from, int size) {
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        List<EventRespShort> events = eventRepository.findByInitiatorId(userId, pageable)
                .stream()
                .map(eventMapper::mapToEventRespShort)
                .toList();

        List<Long> eventIds = events.stream().map(EventRespShort::getId).toList();

        Map<Long, Long> confirmedRequestsByEvents = requestRepository
                .countByEventIdInAndStatusGroupByEvent(eventIds, String.valueOf(RequestStatus.CONFIRMED))
                .stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));

        List<Long> views = ConnectToStatServer.getViews(Constants.DEFAULT_START_TIME, Constants.DEFAULT_END_TIME,
                ConnectToStatServer.prepareUris(eventIds), true, statisticClient);

        List<? extends EventRespShort> eventsForResp =
                Utilities.addViewsAndConfirmedRequests(events, confirmedRequestsByEvents, views);

        return Utilities.checkTypes(eventsForResp,
                EventRespShort.class);
    }

    @Override
    public EventRespFull getUsersFullEventById(long userId, long eventId, String path) {
        Event event = validateAndGetEvent(eventId);
        long confirmedRequests = requestRepository
                .countByEventIdAndStatus(eventId, String.valueOf(RequestStatus.CONFIRMED));
        EventRespFull eventRespFull = eventMapper.mapToEventRespFull(event);
        eventRespFull.setConfirmedRequests(confirmedRequests);
        List<Long> views = ConnectToStatServer.getViews(Constants.DEFAULT_START_TIME,
                Constants.DEFAULT_END_TIME, path, true, statisticClient);
        if (views.isEmpty()) {
            eventRespFull.setViews(0L);
            return eventRespFull;
        }
        eventRespFull.setViews(views.getFirst());
        return eventRespFull;
    }

    @Override
    @Transactional
    public EventRequest updateUsersEvent(long userId, long eventId, EventUpdate eventUpdate) {
        Event updatingEvent = validateAndGetEvent(eventId);
        checkAbilityToUpdate(updatingEvent);

        if (eventUpdate.getEventDate() != null) {
            validateEventDate(eventUpdate.getEventDate());
        }

        Category category = updatingEvent.getCategory();
        if (eventUpdate.getCategory() != null) {
            category = validateAndGetCategory(eventUpdate.getCategory());
        }

        Event updatedEvent = eventMapper.updateEvent(updatingEvent, eventUpdate, category);
        return eventMapper.mapToEventRequest(updatedEvent);
    }

    @Override
    public Collection<RequestDto> getRequestsByEventId(long eventId, long userId) {
        validateAndGetEvent(eventId);
        return requestRepository.findByEventId(eventId)
                .stream()
                .map(requestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestResponse approveRequests(RequestsForConfirmation requestsForConfirmation,
                                           long userId,
                                           long eventId) {
        Event event = validateAndGetEvent(eventId);

        List<Requests> requests = requestRepository
                .findByIdInAndEventId(requestsForConfirmation.getRequestIds(), eventId);

        checkRequestStatus(requests);

        int participants = countParticipants(eventId);
        checkParticipantsLimit(event.getParticipantLimit(), participants);
        int freeSlots = event.getParticipantLimit() - participants;

        if (freeSlots >= requests.size()) {
            List<RequestDto> approvedRequest = setStatusToRequests(RequestStatus
                    .valueOf(requestsForConfirmation.getStatus()), requests)
                    .stream()
                    .map(requestMapper::mapToRequestDto)
                    .toList();
            RequestResponse response = RequestResponse.builder().build();
            if (requestsForConfirmation.getStatus().equals(String.valueOf(RequestStatus.REJECTED))) {
                response.setRejectedRequests(approvedRequest);
                response.setConfirmedRequests(List.of());
            } else {
                response.setRejectedRequests(List.of());
                response.setConfirmedRequests(approvedRequest);
            }
            return response;
        }

        List<Requests> requestsToCancel = setStatusToRequests(RequestStatus.REJECTED,
                requests.subList(freeSlots, requests.size()));

        List<RequestDto> confirmed = setStatusToRequests(RequestStatus
                .valueOf(requestsForConfirmation.getStatus()), requests.subList(0, freeSlots))
                .stream()
                .map(requestMapper::mapToRequestDto)
                .toList();

        List<RequestDto> rejected = setStatusToRequests(RequestStatus.REJECTED,
                requests.subList(freeSlots, requests.size())) //requestRepository.saveAll()
                .stream()
                .map(requestMapper::mapToRequestDto)
                .toList();

        return RequestResponse
                .builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }

    private List<Requests> setStatusToRequests(RequestStatus status, List<Requests> requests) {
        for (Requests requestToApprove : requests) {
            requestToApprove.setStatus(String.valueOf(status));
        }
        return requests;
    }

    private int countParticipants(long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId,
                String.valueOf(RequestStatus.CONFIRMED));
    }

    private void checkParticipantsLimit(long participantsLimit, long participants) {
        if (participantsLimit < (participants + 1)) {
            log.warn("Невозможно добавить запрос. Лимит участников {} меньше чем запросов {}",
                    participantsLimit, (participants + 1));
            throw new ConflictException("Превышено количество запросов");
        }
    }

    private void checkRequestStatus(List<Requests> request) {
        int leftIdx = 0;
        int rightIdx = request.size() - 1;
        while (leftIdx <= rightIdx) {

            if (!request.get(leftIdx).getStatus().equals(RequestStatus.PENDING.name())) {
                log.warn("Статус должен быть PENDING");
                throw new ConflictException("Запрос с id = " + request.get(leftIdx).getId() + " со статусом: "
                        + request.get(leftIdx).getStatus());
            }

            if (!request.get(rightIdx).getStatus().equals(RequestStatus.PENDING.name())) {
                log.warn("Статус может быть только PENDING");
                throw new ConflictException("Запрос с id: " + request.get(rightIdx).getId() + " со статусом: "
                        + request.get(rightIdx).getStatus());
            }
            leftIdx++;
            rightIdx--;
        }
    }

    private void addLocation(Location location) {
        locationRepository.save(location);
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Дата события ({}) меньше чем через 2 часа", eventDate);
            throw new ConflictException("Дата события " + eventDate + " меньше чем через 2 часа");
        }
    }

    private Event validateAndGetEvent(long eventId) {

        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private User validateAndGetUser(long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Category validateAndGetCategory(int categoryId) {

        return categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + categoryId + " не найдена"));
    }

    private void checkAbilityToUpdate(Event event) {
        if (!(event.getState().equals(String.valueOf(EventStates.PENDING)))
                && !(event.getState().equals(String.valueOf(EventStates.CANCELED)))) {
            log.warn("Обновление невозможно, состояние события: {}", event.getState());
            throw new ConflictException("Состояние должно быть " + EventStates.PENDING + " или " +
                    EventStates.CANCELED);
        }
    }
}
