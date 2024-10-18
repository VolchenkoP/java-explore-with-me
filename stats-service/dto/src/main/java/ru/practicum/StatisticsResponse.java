package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsResponse {
    private Object app;
    private String uri;
    private Long hits;
}
