package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StatisticsDto {

    @NotBlank(message = "empty app name")
    private String app;

    @NotBlank(message = "empty uri")
    private String uri;

    @NotBlank(message = "empty ip")
    private String ip;

    @NotNull(message = "time is null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
