package ru.practicum.common.constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String DATA_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATA_PATTERN);

    public static final LocalDateTime DEFAULT_START_TIME = LocalDateTime.parse("1000-12-12 12:12:12",
            Constants.DATE_FORMATTER);
    public static final LocalDateTime DEFAULT_END_TIME = LocalDateTime.parse("3000-12-12 12:12:12",
            Constants.DATE_FORMATTER);
}
