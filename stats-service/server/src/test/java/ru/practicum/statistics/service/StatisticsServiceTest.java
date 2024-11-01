package ru.practicum.statistics.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatisticsDto;
import ru.practicum.StatisticsResponse;
import ru.practicum.constants.Constants;
import ru.practicum.model.App;
import ru.practicum.service.StatisticsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatisticsServiceTest {

    private static StatisticsDto statEvent1;
    private static StatisticsDto statEvent2;
    private static StatisticsDto statEvent3;
    private static StatisticsDto statEvent4;
    private static App mainApp;
    private static LocalDateTime start = LocalDateTime.parse("2000-01-01 11:11:11", Constants.DATE_FORMATTER);
    private static LocalDateTime end = LocalDateTime.parse("3000-01-01 11:11:11", Constants.DATE_FORMATTER);
    private final EntityManager em;
    private final StatisticsService statisticService;

    @BeforeAll
    static void setup() {
        mainApp = new App();
        mainApp.setId(1L);
        mainApp.setName("ewm-main-service");

        statEvent1 = StatisticsDto
                .builder()
                .uri("event/1")
                .ip("1")
                .app(mainApp.getName())
                .timestamp((LocalDateTime.parse("2023-10-06 22:00:23", Constants.DATE_FORMATTER)))
                .build();

        statEvent2 = StatisticsDto
                .builder()
                .uri("event/2")
                .ip("1")
                .app(mainApp.getName())
                .timestamp((LocalDateTime.parse("2023-11-06 20:00:23", Constants.DATE_FORMATTER)))
                .build();

        statEvent3 = StatisticsDto
                .builder()
                .uri("event/1")
                .ip("2")
                .app(mainApp.getName())
                .timestamp((LocalDateTime.parse("2022-11-06 22:00:23", Constants.DATE_FORMATTER)))
                .build();

        statEvent4 = StatisticsDto
                .builder()
                .uri("event/")
                .ip("2")
                .app(mainApp.getName())
                .timestamp((LocalDateTime.parse("2021-10-06 22:00:23", Constants.DATE_FORMATTER)))
                .build();
    }

    @BeforeEach
    void addStats() {
        statisticService.createStatistics(statEvent1);
        statisticService.createStatistics(statEvent2);
        statisticService.createStatistics(statEvent3);
        statisticService.createStatistics(statEvent4);
    }

    @Test
    void shouldGetStatForAllEndPointsAndAllIp() {

        List<StatisticsResponse> stats = statisticService.getStatistics(start, end, null, false);
        assertThat(stats.size(), equalTo(3));
        assertThat(stats.get(0).getUri(), equalTo(statEvent1.getUri()));
        assertThat(stats.get(0).getHits(), equalTo(2L));

        assertThat(stats.get(1).getUri(), equalTo(statEvent4.getUri()));
        assertThat(stats.get(1).getHits(), equalTo(1L));

        assertThat(stats.get(2).getUri(), equalTo(statEvent2.getUri()));
        assertThat(stats.get(2).getHits(), equalTo(1L));

    }

    @Test
    void shouldGetStatForAllEndPointsAndUniqueIp() {

        List<StatisticsResponse> stats = statisticService.getStatistics(start, end, null, true);

        assertThat(stats.size(), equalTo(3));
        assertThat(stats.get(0).getUri(), equalTo(statEvent1.getUri()));
        assertThat(stats.get(0).getHits(), equalTo(2L));

        assertThat(stats.get(1).getUri(), equalTo(statEvent4.getUri()));
        assertThat(stats.get(1).getHits(), equalTo(1L));

        assertThat(stats.get(2).getUri(), equalTo(statEvent2.getUri()));
        assertThat(stats.get(2).getHits(), equalTo(1L));
    }

    @Test
    void shouldGetStatForEndPointsListAndAllIp() {

        List<StatisticsResponse> stats = statisticService.getStatistics(start, end, List.of("event/1"), false);

        assertThat(stats.size(), equalTo(1));
        assertThat(stats.get(0).getUri(), equalTo(statEvent1.getUri()));
        assertThat(stats.get(0).getHits(), equalTo(2L));
    }

    @Test
    void shouldGetStatForEndPointsListAndUniqueIp() {
        List<StatisticsResponse> stats = statisticService.getStatistics(start, end, List.of("event/1", "events/2", "event/"), true);

        assertThat(stats.size(), equalTo(2));
        assertThat(stats.get(0).getUri(), equalTo(statEvent1.getUri()));
        assertThat(stats.get(0).getHits(), equalTo(2L));

        assertThat(stats.get(1).getUri(), equalTo(statEvent4.getUri()));
        assertThat(stats.get(1).getHits(), equalTo(1L));
    }
}