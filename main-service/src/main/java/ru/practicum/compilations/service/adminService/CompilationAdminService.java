package ru.practicum.compilations.service.adminService;

import ru.practicum.compilations.dto.CompilationRequest;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.dto.CompilationUpdated;

public interface CompilationAdminService {

    CompilationResponse addCompilation(CompilationRequest compilationRequest);

    CompilationResponse updateCompilation(int id, CompilationUpdated compilationUpdated);

    void deleteCompilation(int id);
}
