package ru.practicum.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.StatisticResponse;
import ru.practicum.exception.ClientException;
import ru.practicum.statisticsClient.StatisticClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ConnectToStatServer {

    public static List<Long> getViews(LocalDateTime start, LocalDateTime end, String uris, boolean unique,
                                      StatisticClient statisticClient) {
        ResponseEntity<List<StatisticResponse>> response = statisticClient.getStat(start, end, uris, unique);


        if (response.getStatusCode().is4xxClientError()) {
            log.warn("Ошибка запроса. Статус: {}", response.getStatusCode());
            throw new ClientException("Bad request. Status code is: " + response.getStatusCode());
        }

        if (response.getStatusCode().is5xxServerError()) {
            log.warn("Внутренняя ошибка сервера с статусом: {}", response.getStatusCode());
            throw new ClientException("Internal server error statusCode is " + response.getStatusCode());
        }

        if (response.getBody() == null) {
            log.warn("Тело ответа пустое");
            throw new ClientException("Returned empty body");
        }

        List<StatisticResponse> statisticResponses = response.getBody();

        return statisticResponses
                .stream()
                .map(StatisticResponse::getHits)
                .collect(Collectors.toList());
    }

    public static String prepareUris(List<Long> ids) {
        return ids
                .stream()
                .map((id) -> "event/" + id).collect(Collectors.joining());
    }
}
