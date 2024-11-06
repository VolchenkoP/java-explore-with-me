package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.StatisticDto;
import ru.practicum.model.Statistics;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {

    @Mapping(target = "app", source = "statistics.app.name")
    StatisticDto toDto(Statistics statistics);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "app", ignore = true)
    Statistics toEntity(StatisticDto dto);

}
