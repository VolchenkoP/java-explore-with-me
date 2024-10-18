package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatisticsDto;
import ru.practicum.StatisticsResponse;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.StatisticsMapper;
import ru.practicum.model.App;
import ru.practicum.model.Statistics;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.StatisticsRepository;
import ru.practicum.utils.constants.Constants;

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
        return mapper.toDto(statisticsRepository.save(statistics));
    }

    @Override
    public List<StatisticsResponse> getStatistics(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = convertStringToLocalDateTime(decoderParameters(start));
        LocalDateTime endTime = convertStringToLocalDateTime(decoderParameters(end));

        if (unique) {
            if (uris == null) {
                log.info("");
                return getStatsForAllEndpointsByUniqueIp(startTime, endTime);
            }
            log.info("");
            return getStatsByUniqueIp(startTime, endTime, uris);
        }
        if (uris == null) {
            log.info("");
            return getStatsForAllEndpointsByAllIp(startTime, endTime);
        }
        log.info("");
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
