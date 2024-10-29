package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.mapper.CategoriesMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoriesRepository;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository repository;
    private final CategoriesMapper mapper;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        return mapper.toDto(repository.save(mapper.toEntity(categoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, int categoryId) {
        Category updatingCategory = validateCategory(categoryId);
        updatingCategory.setName(categoryDto.getName());
        return mapper.toDto(updatingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(int categoryId) {
        validateCategory(categoryId);
        repository.deleteById(categoryId);
    }

    @Override
    public Collection<CategoryDto> getAllCategories(int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size, sortById);
        return repository.findAll(pageable)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(int categoryId) {
        Category category = validateCategory(categoryId);
        return mapper.toDto(category);
    }

    private Category validateCategory(int categoryId) {
        Optional<Category> category = repository.findById(categoryId);

        if (category.isEmpty()) {
            log.warn(": {}", categoryId);
            throw new NotFoundException("id = " + categoryId + " ");
        }
        return category.get();
    }
}
