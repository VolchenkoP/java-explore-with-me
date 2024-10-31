package ru.practicum.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.practicum.statisticsClient.StatisticsClient;

@Component
@RequiredArgsConstructor
public class Configuration {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${stats-server.url}")
    private String serverUri;

    @Bean
    public StatisticsClient statisticClient() {
        return new StatisticsClient(serverUri, restTemplateBuilder);
    }

    @Bean
    public String serverUrl() {
        return serverUri;
    }
}
