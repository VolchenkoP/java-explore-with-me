package ru.practicum.compilations.service.publicService;

import ru.practicum.compilations.dto.CompilationResponse;

import java.util.List;

public interface CompilationPublicService {

    CompilationResponse getCompilationById(int id);

    List<CompilationResponse> getCompilations(boolean pinned, int from, int size);
}