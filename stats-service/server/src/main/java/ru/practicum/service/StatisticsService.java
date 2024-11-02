package ru.practicum.service;

import ru.practicum.StatisticDto;
import ru.practicum.StatisticResponse;

import java.util.List;

public interface StatisticsService {
    StatisticDto createStatistics(StatisticDto dto);

    List<StatisticResponse> getStatistics(String start, String end, List<String> uris, boolean unique);
}
