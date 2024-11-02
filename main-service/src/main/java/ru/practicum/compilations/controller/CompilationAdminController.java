package ru.practicum.compilations.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilations.dto.CompilationRequest;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.dto.CompilationUpdated;
import ru.practicum.compilations.service.adminService.CompilationAdminService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {

    private final CompilationAdminService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponse addCompilation(@Valid @RequestBody CompilationRequest compilationRequest) {
        log.info("CompilationAdminController, addCompilation, compilationRequest: {}", compilationRequest);
        return compilationService.addCompilation(compilationRequest);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponse updateCompilation(@PathVariable("id") int id,
                                                 @Valid @RequestBody CompilationUpdated compilationUpdate) {
        log.info("CompilationAdminController, updateCompilation, compId: {}, compilationRequest: {}",
                id, compilationUpdate);
        return compilationService.updateCompilation(id, compilationUpdate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("id") int id) {
        log.info("CompilationAdminController, deleteCompilation, id: {}", id);
        compilationService.deleteCompilation(id);
    }

}
