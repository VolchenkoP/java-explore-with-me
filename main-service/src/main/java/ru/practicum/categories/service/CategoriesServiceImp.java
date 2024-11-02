package ru.practicum.categories.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.mapper.CategoriesMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoriesRepository;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriesServiceImp implements CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final CategoriesMapper categoriesMapper;

    @Override
    public CategoryDto addCategory(@Valid CategoryDto categoryDto) {
        return categoriesMapper
                .mapToCategoryDto(categoriesRepository.save(categoriesMapper.mapToCategory(categoryDto)));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, int categoryId) {
        Category updatingCategory = validateCategory(categoryId);
        updatingCategory.setName(categoryDto.getName());
        return categoriesMapper.mapToCategoryDto(categoriesRepository.save(updatingCategory));
    }

    @Override
    public void deleteCategory(int categoryId) {
        validateCategory(categoryId);
        categoriesRepository.deleteById(categoryId);
    }

    @Override
    public Collection<CategoryDto> getAllCategories(int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size, sortById);
        return categoriesRepository.findAll(pageable)
                .stream()
                .map(categoriesMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(int categoryId) {
        Category category = validateCategory(categoryId);
        return categoriesMapper.mapToCategoryDto(category);
    }

    private Category validateCategory(int categoryId) {
        Optional<Category> category = categoriesRepository.findById(categoryId);

        if (category.isEmpty()) {
            log.warn("Попытка удалить несуществующую категорию с id: {}", categoryId);
            throw new NotFoundException("Категория с id = " + categoryId + " не найдена");
        }
        return category.get();
    }
}
