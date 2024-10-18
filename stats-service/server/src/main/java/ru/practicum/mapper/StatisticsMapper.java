package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.StatisticsDto;
import ru.practicum.model.Statistics;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {
    @Mapping(target = "app", source = "statistics.app.name")
    StatisticsDto toDto(Statistics statistics);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "app", ignore = true)
    Statistics toEntity(StatisticsDto dto);

}
