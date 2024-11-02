package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.categories.model.Category;
import ru.practicum.common.constants.Constants;
import ru.practicum.events.model.Location;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponse {

    private Long id;

    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Category category;

    @Min(0)
    private Long confirmedRequests;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATA_PATTERN)
    private LocalDateTime createdOn;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @NotNull
    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATA_PATTERN)
    private LocalDateTime eventDate;

    @NotNull
    private User initiator;

    @NotNull
    private Location location;

    @NotNull
    private Boolean paid;

    @Min(value = 0)
    private Integer participantLimit;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATA_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;
    private String state;

    @NotBlank
    @Length(min = 3, max = 120)
    private String title;

    private Long views;
}
