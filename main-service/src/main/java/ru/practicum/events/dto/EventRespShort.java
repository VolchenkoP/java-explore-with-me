package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.practicum.categories.model.Category;
import ru.practicum.common.constants.Constants;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class EventRespShort {

    private Long id;

    @NotBlank(message = "empty annotation")
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull(message = "category must be existed")
    private Category category;

    @Min(0)
    private Long confirmedRequests;

    @NotNull(message = "event date must be existed")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATA_PATTERN)
    private LocalDateTime eventDate;

    @NotNull
    private User initiator;

    private Boolean paid;

    @NotBlank(message = "empty title")
    @Length(min = 3, max = 120)
    private String title;

    @Min(0)
    private Long views;
}
