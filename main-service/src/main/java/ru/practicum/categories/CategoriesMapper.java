package ru.practicum.categories;

import org.mapstruct.Mapper;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoriesMapper {

    CategoryDto mapToCategoryDto(Category category);

    Category mapToCategory(CategoryDto dto);

}
