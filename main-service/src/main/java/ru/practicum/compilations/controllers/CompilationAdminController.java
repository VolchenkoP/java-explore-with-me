package ru.practicum.compilations.controllers;

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
import ru.practicum.compilations.dto.CompilationUpdate;
import ru.practicum.compilations.services.CompilationAdminService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {

    private final CompilationAdminService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponse addCompilation(@Valid @RequestBody CompilationRequest compilationRequest) {
        log.info("Добавление новой компиляции с запросом: {}", compilationRequest);
        return compilationService.addCompilation(compilationRequest);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponse updateCompilation(@PathVariable("id") int id,
                                                 @Valid @RequestBody CompilationUpdate compilationUpdate) {
        log.info("Обноление компиляции с id: {} и запросом: {}",
                id, compilationUpdate);
        return compilationService.updateCompilation(id, compilationUpdate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("id") int id) {
        log.info("Удаление компиляции с id: {}", id);
        compilationService.deleteCompilation(id);
    }


}
