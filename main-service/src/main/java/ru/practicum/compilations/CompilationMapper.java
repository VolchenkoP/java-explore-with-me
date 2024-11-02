package ru.practicum.compilations;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilations.dto.CompilationRequest;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.dto.CompilationUpdate;
import ru.practicum.compilations.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    Compilation mapToCompilation(CompilationRequest request);

    Compilation mapToCompilation(CompilationUpdate compilationUpdated);

    @Mapping(target = "events", ignore = true)
    CompilationResponse mapToCompilationResponse(Compilation compilation);

    default Compilation updateCompilation(Compilation updatingCompilation, Compilation newCompilation) {
        if (newCompilation.getTitle() != null) {
            updatingCompilation.setTitle(newCompilation.getTitle());
        }
        if (newCompilation.getPinned() != null) {
            updatingCompilation.setPinned(newCompilation.getPinned());
        }
        return updatingCompilation;
    }
}
