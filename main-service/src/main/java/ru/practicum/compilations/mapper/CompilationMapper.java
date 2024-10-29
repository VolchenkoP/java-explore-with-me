package ru.practicum.compilations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilations.dto.CompilationRequest;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.dto.CompilationUpdated;
import ru.practicum.compilations.model.Compilation;

@Mapper
public interface CompilationMapper {

    Compilation toEntity(CompilationRequest request);

    Compilation toEntity(CompilationUpdated compilationUpdated);

    @Mapping(target = "events", ignore = true)
    CompilationResponse toResponse(Compilation compilation);
}
