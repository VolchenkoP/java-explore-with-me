package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatisticsDto;
import ru.practicum.StatisticsResponse;
import ru.practicum.constants.Constants;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.StatisticsMapper;
import ru.practicum.model.App;
import ru.practicum.model.Statistics;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.StatisticsRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final AppRepository appRepository;
    private final StatisticsMapper mapper;

    @Override
    public StatisticsDto createStatistics(StatisticsDto dto) {
        App app = validationApp(dto.getApp());
        Statistics statistics = mapper.toEntity(dto);
        statistics.setApp(app);
        log.info("Данные успешно добавлены в статистику");
        return mapper.toDto(statisticsRepository.save(statistics));
    }

    @Override
    public List<StatisticsResponse> getStatistics(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = convertStringToLocalDateTime(decoderParameters(start));
        log.info("Параметр даты начала успешно сконвертирован");
        LocalDateTime endTime = convertStringToLocalDateTime(decoderParameters(end));
        log.info("Параметр даты окончания успешно сконвертирован");

        if (unique) {
            if (uris == null || uris.isEmpty()) {
                log.info("Поиск всей статистики для уникального IP");
                return getStatsForAllEndpointsByUniqueIp(startTime, endTime);
            }
            log.info("Поиск статистики для уникального IP и списка ссылок");
            return getStatsByUniqueIp(startTime, endTime, uris);
        }
        if (uris == null || uris.isEmpty()) {
            log.info("Поиск всей статистики");
            return getStatsForAllEndpointsByAllIp(startTime, endTime);
        }
        log.info("Поиск статистики по списку ссылок");
        return getStatsByAllIp(startTime, endTime, uris);
    }

    private List<StatisticsResponse> getStatsByUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return statisticsRepository.findByUriInAndStartBetweenUniqueIp(uris, start, end);
    }

    private List<StatisticsResponse> getStatsByAllIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return statisticsRepository.findByUriInAndStartBetween(uris, start, end);
    }

    private List<StatisticsResponse> getStatsForAllEndpointsByUniqueIp(LocalDateTime start, LocalDateTime end) {
        return statisticsRepository.findStartBetweenUniqueIp(start, end);
    }

    private List<StatisticsResponse> getStatsForAllEndpointsByAllIp(LocalDateTime start, LocalDateTime end) {
        return statisticsRepository.findStartBetween(start, end);
    }

    private App validationApp(String appName) {
        log.info("Поиск сервиса с именем: {}", appName);
        return appRepository.findByName(appName)
                .orElseThrow(() -> new NotFoundException("Сервис с именем " + appName + " не найден"));
    }

    private String decoderParameters(String parameter) {
        return URLDecoder.decode(parameter, StandardCharsets.UTF_8);
    }

    private LocalDateTime convertStringToLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, Constants.DATE_FORMATTER);
    }
}
