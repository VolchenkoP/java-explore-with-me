package ru.practicum.categories.mapper;

import org.mapstruct.Mapper;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoriesMapper {

    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto dto);

}
