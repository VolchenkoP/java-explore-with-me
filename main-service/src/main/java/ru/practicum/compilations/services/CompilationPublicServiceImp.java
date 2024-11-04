package ru.practicum.compilations.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.dto.EventByCompId;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.compilations.repository.EventByCompilationRepository;
import ru.practicum.events.dto.EventRespShort;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationPublicServiceImp implements CompilationPublicService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventByCompilationRepository eventByCompilationRepository;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationResponse getCompilationById(int compId) {
        Compilation compilation = validateAndCompilation(compId);

        List<Long> eventIds = eventByCompilationRepository.findByCompilationId(compId);

        List<EventRespShort> events = eventRepository.findByIdIn(eventIds)
                .stream()
                .map(eventMapper::mapToEventRespShort)
                .toList();

        CompilationResponse compilationResponse = compilationMapper.mapToCompilationResponse(compilation);
        compilationResponse.setEvents(events);
        return compilationResponse;
    }

    @Override
    public List<CompilationResponse> getCompilations(boolean pinned, int from, int size) {
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        //Find all compilations
        Map<Integer, Compilation> compilationMap = compilationRepository.findAll(pageable)
                .stream()
                .collect(Collectors.toMap(Compilation::getId, Function.identity()));

        List<EventByCompId> eventsByCompIdIn = eventByCompilationRepository
                .findEventsByCompIdIn(compilationMap.keySet());

        Map<Integer, List<EventRespShort>> eventShortListByCompId = new HashMap<>();

        for (EventByCompId eventByCompId : eventsByCompIdIn) {
            if (!eventShortListByCompId.containsKey(eventByCompId.getCompilationId())) {
                Event event = eventByCompId.getEvent();
                List<EventRespShort> events;
                if (event != null) {
                    events = new ArrayList<>();
                    events.add(eventMapper.mapToEventRespShort(eventByCompId.getEvent()));
                } else {
                    events = List.of();
                }
                eventShortListByCompId.put(eventByCompId.getCompilationId(), events);
                continue;
            }
            if (eventByCompId.getEvent() == null) {
                continue;
            }
            eventShortListByCompId.get(eventByCompId.getCompilationId())
                    .add(eventMapper.mapToEventRespShort(eventByCompId.getEvent()));
        }

        List<CompilationResponse> compilationResponses = new ArrayList<>();

        for (Compilation compilation : compilationMap.values()) {
            List<EventRespShort> events = eventShortListByCompId.get(compilation.getId());
            if (events == null) {
                events = List.of();
            }
            CompilationResponse compilationResponse = compilationMapper.mapToCompilationResponse(compilation);
            compilationResponse.setEvents(events);
            compilationResponses.add(compilationResponse);
        }
        return compilationResponses;
    }

    private Compilation validateAndCompilation(int compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);

        if (compilation.isEmpty()) {
            log.warn("Компиляция с id: {} не найдена", compId);
            throw new NotFoundException("Компиляция с id: " + compId + " не найдена");
        }
        return compilation.get();
    }
}
