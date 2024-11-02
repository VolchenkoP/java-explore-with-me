package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.events.dto.EventResponseShort;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationResponse {

    private Integer id;

    @NotBlank(message = "empty title")
    @Length(max = 50)
    private String title;

    @NotNull(message = "pinned must not be null")
    private Boolean pinned;

    private List<EventResponseShort> events;
}
