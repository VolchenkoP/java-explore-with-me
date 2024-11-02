package ru.practicum.events.service.publicService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.common.config.ConnectStatsServer;
import ru.practicum.common.constants.Constants;
import ru.practicum.common.utilities.Utilities;
import ru.practicum.events.dto.EventResponse;
import ru.practicum.events.dto.EventResponseShort;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStates;
import ru.practicum.events.repository.EventsRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dto.EventIdByRequestsCount;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestsRepository;
import ru.practicum.statisticsClient.StatisticsClient;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {

    private final EventsRepository eventRepository;
    private final RequestsRepository requestRepository;
    private final StatisticsClient statisticClient;

    @Override
    public Collection<EventResponseShort> searchEvents(String text, List<Integer> categories, Boolean paid,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                       boolean onlyAvailable, String sort, int from, int size) {
        validateDates(rangeStart, rangeEnd);
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        if (text == null) {
            text = "";
        }
        if (categories == null) {
            categories = List.of();
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = Constants.defaultEndTime;
        }

        List<EventResponseShort> events = eventRepository
                .searchEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable)
                .stream()
                .map(EventMapper::toResponseShort)
                .toList();

        List<Long> eventsIds = events.stream()
                .map(EventResponseShort::getId)
                .toList();

        Map<Long, Long> confirmedRequestsByEvents = requestRepository
                .countByEventIdInAndStatusGroupByEvent(eventsIds, String.valueOf(RequestStatus.CONFIRMED))
                .stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));

        System.out.println(confirmedRequestsByEvents);

        List<Long> views = ConnectStatsServer.getViews(Constants.defaultStartTime,
                Constants.defaultEndTime, ConnectStatsServer.prepareUris(eventsIds),
                true, statisticClient);

        List<? extends EventResponseShort> eventsForResp =
                Utilities.addViewsAndConfirmedRequests(events, confirmedRequestsByEvents, views);

        System.out.println(eventsForResp);
        System.out.println(eventsForResp.getFirst().getConfirmedRequests());

        return Utilities.checkTypes(eventsForResp, EventResponseShort.class);
    }

    @Override
    public EventResponse getEvent(long eventId, String path) {
        Event event = eventRepository.findByIdAndState(eventId, String.valueOf(EventStates.PUBLISHED))
                .orElseThrow(() -> {
                    log.warn("Attempt to get unknown event");
                    return new NotFoundException("Event with id = " + eventId + "was not found");
                });

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId,
                String.valueOf(RequestStatus.CONFIRMED));

        EventResponse eventFull = EventMapper.toResponse(event);
        eventFull.setConfirmedRequests(confirmedRequests);
        List<Long> views = ConnectStatsServer.getViews(Constants.defaultStartTime,
                Constants.defaultEndTime, path,
                true, statisticClient);
        if (views.isEmpty()) {
            eventFull.setViews(0L);
        }
        eventFull.setViews(views.get(0));
        return eventFull;
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return;
        }
        if (start.isAfter(end)) {
            log.warn("Prohibited. Start is after end. Start: {}, end: {}", start, end);
            throw new ValidationException("Event must be published");
        }
    }
}
