package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.StatisticsDto;
import ru.practicum.StatisticsResponse;
import ru.practicum.service.StatisticsService;
import ru.practicum.utils.constants.Constants;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService service;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatisticsResponse> getStatistics(@RequestParam("start")
                                                  @DateTimeFormat(pattern = Constants.DATE_PATTERN) String start,
                                                  @RequestParam("end")
                                                  @DateTimeFormat(pattern = Constants.DATE_PATTERN) String end,
                                                  @RequestParam(required = false, value = "uris") List<String> uris,
                                                  @RequestParam(required = false, value = "unique") boolean unique) {
        return service.getStatistics(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticsDto createStatistics(@Valid @RequestBody StatisticsDto statisticsDto) {
        return service.createStatistics(statisticsDto);
    }
}
