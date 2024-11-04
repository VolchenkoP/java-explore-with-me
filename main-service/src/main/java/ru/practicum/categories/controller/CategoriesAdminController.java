package ru.practicum.categories.controller;

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
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoriesService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoriesAdminController {

    private final CategoriesService categoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Добавление новой категории с параметрами id: {}. name: {}",
                categoryDto.getId(), categoryDto.getName());
        return categoriesService.addCategory(categoryDto);
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                      @PathVariable(value = "categoryId") int categoryId) {
        log.info("Обновление категории с параметрами id: {}. name: {}",
                categoryDto.getId(), categoryDto.getName());
        return categoriesService.updateCategory(categoryDto, categoryId);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "categoryId") int categoryId) {
        log.info("Удаление категории с id: {}", categoryId);
        categoriesService.deleteCategory(categoryId);
    }
}
