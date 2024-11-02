package ru.practicum.compilations.controllers;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.services.CompilationPublicService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicController {

    private final CompilationPublicService compilationService;

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponse getCompilationById(@PathVariable("compId") int compId) {
        log.info("Поиск компиляции с id: {}", compId);
        return compilationService.getCompilationById(compId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationResponse> getCompilations(@RequestParam(value = "pinned", required = false) boolean pinned,
                                                     @RequestParam(value = "from", defaultValue = "0")
                                                     @Min(0) int from,
                                                     @RequestParam(value = "size", defaultValue = "10")
                                                     @Min(0) int size) {
        log.info("Поиск компиляций с параметрами pinned: {}, from: {}, size: {}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }
}
