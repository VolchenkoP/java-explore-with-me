package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatisticsDto;
import ru.practicum.StatisticsResponse;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.StatisticsMapper;
import ru.practicum.model.App;
import ru.practicum.model.Statistics;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.StatisticsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticRepository;
    private final AppRepository appRepository;
    private final StatisticsMapper statisticsMapper;

    @Override
    public StatisticsDto createStatistics(StatisticsDto statisticDto) {
        App app = checkApp(statisticDto.getApp());
        Statistics statistic = statisticsMapper.toEntity(statisticDto);
        statistic.setApp(app);
        return statisticsMapper.toDto(statisticRepository.save(statistic));
    }

    @Override
    public List<StatisticsResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validateDates(start, end);
        if (unique) {
            if (uris == null) {
                log.info("Finding stats for unique IP and for all URI");
                return getStatsForAllEndpointsByUniqueIp(start, end);
            }
            log.info("Finding stats for unique IP and for List of URI");
            return getStatsByUniqueIp(start, end, uris);
        }

        if (uris == null) {
            log.info("Finding stats for all IP and for all URI");
            return getStatsForAllEndpointsByAllIp(start, end);
        }

        log.info("Finding stats for all IP and for List of URI");
        return getStatsByAllIp(start, end, uris);
    }

    private List<StatisticsResponse> getStatsByUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return statisticRepository.findByUriInAndStartBetweenUniqueIp(uris, start, end);
    }

    private List<StatisticsResponse> getStatsByAllIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return statisticRepository.findByUriInAndStartBetween(uris, start, end);
    }

    private List<StatisticsResponse> getStatsForAllEndpointsByUniqueIp(LocalDateTime start, LocalDateTime end) {
        return statisticRepository.findStartBetweenUniqueIp(start, end);
    }

    private List<StatisticsResponse> getStatsForAllEndpointsByAllIp(LocalDateTime start, LocalDateTime end) {
        return statisticRepository.findStartBetween(start, end);
    }

    private App checkApp(String appName) {
        Optional<App> app = appRepository.findByName(appName);
        if (app.isEmpty()) {
            log.warn("Adding app name is not existed. App name: {}", appName);
            throw new NotFoundException("Bad required app name");
        }
        return app.get();
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return;
        }
        if (start.isAfter(end)) {
            log.warn("Start is after end");
            throw new ValidationException("Start is after end");
        }

    }
}
