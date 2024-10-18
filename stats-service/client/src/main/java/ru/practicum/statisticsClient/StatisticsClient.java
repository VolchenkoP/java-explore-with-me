package ru.practicum.statisticsClient;

import jakarta.annotation.Nullable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.BaseClient;
import ru.practicum.StatisticsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsClient extends BaseClient {
    public StatisticsClient(RestTemplateBuilder builder, String serverUrl) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> getStatistics(LocalDateTime start, LocalDateTime end,
                                                @Nullable List<String> uris, boolean unique) {
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
        return get("/stat" + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> addStatistics(StatisticsDto dto) {
        return post("/hit", dto, null);
    }

    private String encodeParameter(String parameter) {
        return URLEncoder.encode(parameter, StandardCharsets.UTF_8);
    }

    private String convertLocalDateTimeToString(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
