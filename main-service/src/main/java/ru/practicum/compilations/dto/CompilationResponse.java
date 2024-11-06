package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.events.dto.EventRespShort;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationResponse {

    private Integer id;

    // @NotBlank(message = "empty title")
    // @Length(max = 50)
    private String title;

    // @NotNull(message = "pinned must not be null")
    private Boolean pinned;

    private List<EventRespShort> events;

}
