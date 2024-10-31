package ru.practicum.compilations.service.adminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.CompilationRequest;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.dto.CompilationUpdate;
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

    @Override
    public CompilationResponse addCompilation(CompilationRequest compilationRequest) {
        if (compilationRequest.getPinned() == null) {
            compilationRequest.setPinned(false);
        }

        //Save to compilation table
        Compilation savedCompilation = compilationRepository
                .save(compilationMapper.toEntity(compilationRequest));

        int compilationId = savedCompilation.getId(); //returned compilation_id

        CompilationResponse compilationResponse = compilationMapper.toResponse(savedCompilation);

        if (compilationRequest.getEvents() == null) {
            compilationResponse.setEvents(List.of());
            return compilationResponse;
        }
        compilationResponse.setEvents(addEventByCompilations(compilationRequest, compilationId));
        return compilationResponse;
    }

    @Override
    public CompilationResponse updateCompilation(int id, CompilationUpdate compilationUpdate) {
        Compilation updatingCompilation = validateAndGetCompilation(id);

        Compilation updatedCompilation = compilationRepository
                .save(compilationMapper.updateCompilation(
                        updatingCompilation,
                        compilationMapper.toEntity(compilationUpdate)
                ));

        CompilationResponse compilationResponse = compilationMapper.toResponse(updatedCompilation);
        if (compilationUpdate.getEvents() == null) {
            compilationResponse.setEvents(List.of());
            return compilationResponse;
        }

        deleteEventsByCompilations(id);

        compilationResponse.setEvents(addEventByCompilations(compilationUpdate, id));
        return compilationResponse;
    }

    @Override
    public void deleteCompilation(int id) {
        validateAndGetCompilation(id);
        compilationRepository.deleteById(id);
        deleteEventsByCompilations(id);
    }

    private <T extends CompilationUpdate> List<EventResponseShort> addEventByCompilations(T compilation, int id) {

        List<EventsByCompilation> eventsByComp = compilation
                .getEvents()
                .stream()
                .map((EbCId) -> new EventsByCompilation(new CompositeKeyForEventByCompilation(id, EbCId)))
                .toList();

        eventByCompilationRepository.saveAll(eventsByComp);

        return eventRepository.findByIdIn(compilation.getEvents())
                .stream()
                .map(eventMapper::toResponseShort)
                .toList();
    }

    private void deleteEventsByCompilations(int id) {
        if (!eventByCompilationRepository.findByCompilationId(id).isEmpty()) {
            eventByCompilationRepository.deleteByCompilationId(id);
        }
    }

    private Compilation validateAndGetCompilation(int id) {
        if (!compilationRepository.existsById(id)) {
            log.warn("Compilation with: {} was not found", id);
            throw new NotFoundException("Compilation with = " + id + " was not found");
        }
        return compilationRepository.findById(id).orElseThrow();
    }
}
}
