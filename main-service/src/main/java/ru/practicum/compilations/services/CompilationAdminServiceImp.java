package ru.practicum.compilations.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.CompilationRequest;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.dto.CompilationUpdate;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.model.CompositeKeyForEventByComp;
import ru.practicum.compilations.model.EventsByCompilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.compilations.repository.EventByCompilationRepository;
import ru.practicum.events.dto.EventRespShort;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminServiceImp implements CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final EventByCompilationRepository eventByCompilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    public CompilationResponse addCompilation(CompilationRequest compilationRequest) {
        if (compilationRequest.getPinned() == null) {
            compilationRequest.setPinned(false);
        }

        Compilation savedCompilation = compilationRepository
                .save(compilationMapper.mapToCompilation(compilationRequest));

        int compilationId = savedCompilation.getId();

        CompilationResponse compilationResponse = compilationMapper.mapToCompilationResponse(savedCompilation);
        if (compilationRequest.getEvents() == null) {
            compilationResponse.setEvents(List.of());
            return compilationResponse;
        }
        List<EventRespShort> eventRespShorts = addEventByCompilations(compilationRequest, compilationId);
        compilationResponse.setEvents(eventRespShorts);
        return compilationResponse;
    }

    @Override
    public CompilationResponse updateCompilation(int id, CompilationUpdate compilationUpdate) {
        Compilation updatingCompilation = validateAndGetCompilation(id);

        Compilation updatedCompilation = compilationRepository
                .save(compilationMapper
                        .updateCompilation(updatingCompilation, compilationMapper.mapToCompilation(compilationUpdate)));

        CompilationResponse compilationResponse = compilationMapper.mapToCompilationResponse(updatedCompilation);
        if (compilationUpdate.getEvents() == null) {
            compilationResponse.setEvents(List.of());
            return compilationResponse;
        }

        deleteEventsByCompilations(id);

        List<EventRespShort> eventRespShorts = addEventByCompilations(compilationUpdate, id);

        compilationResponse.setEvents(eventRespShorts);
        return compilationResponse;
    }

    @Override
    public void deleteCompilation(int id) {
        validateAndGetCompilation(id);
        compilationRepository.deleteById(id);
        deleteEventsByCompilations(id);
    }

    private <T extends CompilationUpdate> List<EventRespShort> addEventByCompilations(T compilation, int id) {

        List<EventsByCompilation> eventsByComp = compilation
                .getEvents()
                .stream()
                .map((EbCId) -> new EventsByCompilation(new CompositeKeyForEventByComp(id, EbCId)))
                .toList();

        eventByCompilationRepository.saveAll(eventsByComp);

        return eventRepository.findByIdIn(compilation.getEvents())
                .stream()
                .map(eventMapper::mapToEventRespShort)
                .toList();
    }

    private void deleteEventsByCompilations(int id) {
        if (!eventByCompilationRepository.findByCompilationId(id).isEmpty()) {
            eventByCompilationRepository.deleteByCompilationId(id);
        }
    }

    private Compilation validateAndGetCompilation(int id) {
        if (!compilationRepository.existsById(id)) {
            log.warn("Компиляция с id: {} не найдена", id);
            throw new NotFoundException("Компиляция с id: " + id + " не найдена");
        }
        return compilationRepository.findById(id).orElseThrow();
    }
}
