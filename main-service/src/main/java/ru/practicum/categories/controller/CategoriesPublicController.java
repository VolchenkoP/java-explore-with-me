package ru.practicum.categories.controller;

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
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoriesService;

import java.util.Collection;

@RestController
@RequestMapping("/categories")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CategoriesPublicController {

    private final CategoriesService categoriesService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CategoryDto> getAllCategories(@RequestParam(value = "from", defaultValue = "0")
                                                    @Min(0) int from,
                                                    @RequestParam(value = "size", defaultValue = "10")
                                                    @Min(1) int size) {
        log.info("Поиск категорий с параметрами from: {}, size: {}", from, size);
        return categoriesService.getAllCategories(from, size);
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable(value = "categoryId") int categoryId) {
        log.info("Поиск категории с id: {}", categoryId);
        return categoriesService.getCategoryById(categoryId);
    }

}
