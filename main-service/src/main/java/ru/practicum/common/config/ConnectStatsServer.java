package ru.practicum.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.StatisticsResponse;
import ru.practicum.exception.ClientException;
import ru.practicum.statisticsClient.StatisticsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ConnectStatsServer {

    public static List<Long> getViews(LocalDateTime start, LocalDateTime end, String uris, boolean unique,
                                      StatisticsClient statisticClient) {
        ResponseEntity<List<StatisticsResponse>> response = statisticClient.getStatistics(start, end, uris, unique);


        if (response.getStatusCode().is4xxClientError()) {
            log.warn("Bad request. Status code is {}", response.getStatusCode());
            throw new ClientException("Bad request. Status code is: " + response.getStatusCode());
        }

        if (response.getStatusCode().is5xxServerError()) {
            log.warn("Internal server error statusCode is {}", response.getStatusCode());
            throw new ClientException("Internal server error statusCode is " + response.getStatusCode());
        }

        if (response.getBody() == null) {
            log.warn("Returned empty body");
            throw new ClientException("Returned empty body");
        }

        List<StatisticsResponse> statisticResponses = response.getBody();

        return statisticResponses
                .stream()
                .map(StatisticsResponse::getHits)
                .collect(Collectors.toList());
    }

    public static String prepareUris(List<Long> ids) {
        return ids
                .stream()
                .map((id) -> "event/" + id).collect(Collectors.joining());
    }
}
