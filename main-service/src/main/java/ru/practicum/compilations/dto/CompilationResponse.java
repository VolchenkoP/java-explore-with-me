package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.events.dto.EventRespShort;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationResponse {

    private Integer id;
    private String title;
    private Boolean pinned;
    private List<EventRespShort> events;

}
