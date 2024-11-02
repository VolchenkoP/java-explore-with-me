package ru.practicum.categories.service;

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
@Slf4j
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository repository;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        return CategoriesMapper.toDto(repository.save(CategoriesMapper.toEntity(categoryDto)));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, int categoryId) {
        Category updatingCategory = validateCategory(categoryId);
        updatingCategory.setName(categoryDto.getName());
        return CategoriesMapper.toDto(repository.save(updatingCategory));
    }

    @Override
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
                .map(CategoriesMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(int categoryId) {
        Category category = validateCategory(categoryId);
        return CategoriesMapper.toDto(category);
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
