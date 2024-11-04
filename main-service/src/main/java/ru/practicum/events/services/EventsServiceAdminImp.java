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
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dto.EventIdByRequestsCount;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.statisticsClient.StatisticClient;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventsServiceAdminImp implements EventsServiceAdmin {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoriesRepository categoriesRepository;
    private final RequestRepository requestRepository;
    private final StatisticClient statisticClient;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventRespFull adminsUpdate(EventUpdate eventUpdate, long eventId) {
        Event updatingEvent = validateAndGetEvent(eventId);

        checkAbilityToUpdate(updatingEvent);

        Category category = updatingEvent.getCategory();

        if (eventUpdate.getCategory() != null) {
            category = validateAndGetCategory(eventUpdate.getCategory());
        }

        if (eventUpdate.getLocation() != null) {
            addLocation(eventUpdate.getLocation());
        }

        Event updatedEvent = eventMapper.updateEvent(updatingEvent, eventUpdate, category); //eventRepository.save()
        long confirmedRequests = requestRepository
                .countByEventIdAndStatus(eventId, String.valueOf(RequestStatus.CONFIRMED));
        EventRespFull eventFull = eventMapper.mapToEventRespFull(updatedEvent);
        eventFull.setConfirmedRequests(confirmedRequests);
        return eventFull;
    }

    @Override
    public Collection<EventRespFull> getEventsByConditionalsForAdmin(List<Long> users,
                                                                     List<String> states,
                                                                     List<Integer> categories,
                                                                     LocalDateTime rangeStart,
                                                                     LocalDateTime rangeEnd,
                                                                     int from,
                                                                     int size) {
        validateDates(rangeStart, rangeEnd);
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        if (states == null) {
            states = List.of();
        }
        if (categories == null) {
            categories = List.of();
        }
        if (users == null) {
            users = List.of();
        }
        if (rangeStart == null) {
            rangeStart = Constants.DEFAULT_START_TIME;
        }
        if (rangeEnd == null) {
            rangeEnd = Constants.DEFAULT_END_TIME;
        }

        List<EventRespFull> eventRespFulls = eventRepository
                .findByConditionals(states, categories, users, rangeStart, rangeEnd, pageable)
                .stream()
                .map(eventMapper::mapToEventRespFull)
                .toList();

        List<Long> eventsIds = eventRespFulls
                .stream()
                .map(EventRespFull::getId)
                .toList();

        Map<Long, Long> confirmedRequestsByEvents = requestRepository
                .countByEventIdInAndStatusGroupByEvent(eventsIds, String.valueOf(RequestStatus.CONFIRMED))
                .stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));

        List<Long> views = ConnectToStatServer.getViews(Constants.DEFAULT_START_TIME,
                Constants.DEFAULT_END_TIME,
                ConnectToStatServer.prepareUris(eventsIds), true, statisticClient);

        List<? extends EventRespShort> events =
                Utilities.addViewsAndConfirmedRequests(eventRespFulls, confirmedRequestsByEvents, views);
        return Utilities.checkTypes(events, EventRespFull.class);
    }

    private void addLocation(Location location) {
        locationRepository.save(location);
    }

    private Event validateAndGetEvent(long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            log.warn("Поиск неизвестного события с id: {}", eventId);
            throw new NotFoundException("Событие с id: " + eventId + " не найдено");
        }
        return event.get();
    }

    private Category validateAndGetCategory(int categoryId) {
        Optional<Category> category = categoriesRepository.findById(categoryId);
        if (category.isEmpty()) {
            log.warn("Поиск неизвестной категории с id: {}", categoryId);
            throw new NotFoundException("Категория с id = " + categoryId + " не найдена");
        }
        return category.get();
    }

    private void checkAbilityToUpdate(Event event) {
        if (event.getState().equals(String.valueOf(EventStates.PUBLISHED))
                || event.getState().equals(String.valueOf(EventStates.CANCELED))) {
            log.warn("Обновление события отклонено, состояние события: {}", event.getState());
            throw new ConflictException("Состояние должно быть " + EventStates.PENDING + " или " +
                    EventStates.CANCELED);
        }
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return;
        }
        if (start.isAfter(end)) {
            log.warn("Отклонено. начало события позже окончания, начало: {}, окончание: {}", start, end);
            throw new ValidationException("Событие не опубликовано");
        }
    }
}
