package ru.practicum.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.practicum.statisticsClient.StatisticsClient;

@Component
public class Configuration {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${server.url}")
    private String serverUri;

    public Configuration(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Bean
    public StatisticsClient statisticClient() {
        return new StatisticsClient(serverUri, restTemplateBuilder);
    }

    @Bean
    public String serverUrl() {
        return serverUri;
    }
}
