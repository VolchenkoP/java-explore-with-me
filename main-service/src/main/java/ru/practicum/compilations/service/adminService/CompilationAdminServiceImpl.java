package ru.practicum.compilations.service.adminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.CompilationRequest;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.dto.CompilationUpdated;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.model.CompositeKeyForEventByCompilation;
import ru.practicum.compilations.model.EventsByCompilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.compilations.repository.EventByCompilationRepository;
import ru.practicum.events.dto.EventResponseShort;
import ru.practicum.events.mapper.EventsMapper;
import ru.practicum.events.repository.EventsRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final EventByCompilationRepository eventByCompilationRepository;
    private final EventsRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventsMapper eventMapper;

    //Data stores normalized in two tables. 1 - compilation (id, title, pinned),
    // 2 - events_by_compilations (compilation_id, event_id)
    @Override
    public CompilationResponse addCompilation(CompilationRequest compilationRequest) {
        if (compilationRequest.getPinned() == null) {
            compilationRequest.setPinned(false);
        }

        //Save to compilation table
        Compilation savedCompilation = compilationRepository
                .save(compilationMapper.toEntity(compilationRequest));

        int compilationId = savedCompilation.getId(); //returned compilation_id

        if (compilationRequest.getEvents() == null) {
            CompilationResponse response = compilationMapper.toResponse(savedCompilation);
            response.setEvents(List.of());
            return response;
        }

        //Prepare List<EventsByCompilation> to add in events_by_compilations
        List<EventsByCompilation> eventsByCompilations = compilationRequest.getEvents()
                .stream()
                .map((id) -> new EventsByCompilation(new CompositeKeyForEventByCompilation(compilationId, id)))
                .toList();

        //Save in events_by_compilations
        eventByCompilationRepository.saveAll(eventsByCompilations);

        List<EventResponseShort> events = eventRepository.findByIdIn(compilationRequest.getEvents())
                .stream()
                .map(eventMapper::toResponseShort)
                .toList();

        CompilationResponse response = compilationMapper.toResponse(savedCompilation);
        response.setEvents(events);
        return response;
    }

    @Override
    public CompilationResponse updateCompilation(int id, CompilationUpdated compilationUpdate) {
        Compilation updatingCompilation = validateAndGetCompilation(id);

        Compilation updatedCompilation = compilationRepository
                .save(updateCompilation(updatingCompilation, compilationMapper.toEntity(compilationUpdate)));

        CompilationResponse response = compilationMapper.toResponse(updatedCompilation);

        if (compilationUpdate.getEvents() == null) {
            response.setEvents(List.of());
            return response;
        }

        deleteEventsByCompilations(id);

        List<EventsByCompilation> updatedEventsByComp = compilationUpdate
                .getEvents()
                .stream()
                .map((EbCId) -> new EventsByCompilation(new CompositeKeyForEventByCompilation(id, EbCId)))
                .toList();

        eventByCompilationRepository.saveAll(updatedEventsByComp);

        List<EventResponseShort> events = eventRepository.findByIdIn(compilationUpdate.getEvents())
                .stream()
                .map(eventMapper::toResponseShort)
                .toList();
        response.setEvents(events);
        return response;

    }

    @Override
    public void deleteCompilation(int id) {
        validateAndGetCompilation(id);
        compilationRepository.deleteById(id);
        deleteEventsByCompilations(id);
    }

    private void deleteEventsByCompilations(int id) {
        if (!eventByCompilationRepository.findByCompilationId(id).isEmpty()) {
            eventByCompilationRepository.deleteByCompilationId(id);
        }
    }

    private Compilation updateCompilation(Compilation updatingCompilation, Compilation newCompilation) {
        if (newCompilation.getTitle() != null) {
            updatingCompilation.setTitle(newCompilation.getTitle());
        }
        if (newCompilation.getPinned() != null) {
            updatingCompilation.setPinned(newCompilation.getPinned());
        }
        return updatingCompilation;
    }

    private Compilation validateAndGetCompilation(int id) {
        if (!compilationRepository.existsById(id)) {
            log.warn("Compilation with: {} was not found", id);
            throw new NotFoundException("Compilation with = " + id + " was not found");
        }
        return compilationRepository.findById(id).orElseThrow();
    }
}
