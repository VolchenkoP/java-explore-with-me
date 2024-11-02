package ru.practicum.service;

import ru.practicum.StatisticsDto;
import ru.practicum.StatisticsResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    StatisticsDto createStatistics(StatisticsDto dto);

    List<StatisticsResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
