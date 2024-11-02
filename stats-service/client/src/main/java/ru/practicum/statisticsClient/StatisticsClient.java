package ru.practicum.statisticsClient;

import jakarta.annotation.Nullable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.BaseClient;
import ru.practicum.StatisticsDto;
import ru.practicum.StatisticsResponse;
import ru.practicum.constants.Constants;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsClient extends BaseClient {
    public StatisticsClient(String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<List<StatisticsResponse>> getStatistics(LocalDateTime start, LocalDateTime end,
                                                                  @Nullable String uris, boolean unique) {
        String encodedStartDate = encodeParameter(convertLocalDateTimeToString(start));
        String encodedEndDate = encodeParameter(convertLocalDateTimeToString(end));

        Map<String, Object> parameters = new HashMap<>(
                Map.of(
                        "start", encodedStartDate,
                        "end", encodedEndDate,
                        "unique", unique
                )
        );
        if (uris != null) {
            parameters.put("uris", uris);
        }
        return getList("/stats" + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters,
                new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<Object> addStatistics(StatisticsDto dto) {
        return post("/hit", dto, null);
    }

    private String encodeParameter(String parameter) {
        return URLEncoder.encode(parameter, StandardCharsets.UTF_8);
    }

    private String convertLocalDateTimeToString(LocalDateTime time) {
        return time.format(Constants.DATE_FORMATTER);
    }
}
