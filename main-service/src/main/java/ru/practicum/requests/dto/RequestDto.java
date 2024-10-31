package ru.practicum.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.common.constants.Constants;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestDto {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATA_PATTERN)
    private LocalDateTime created;

    @NotNull
    @Min(0)
    private Long event;

    @NotNull
    @Min(0)
    private Long requester;

    private String status;
}
