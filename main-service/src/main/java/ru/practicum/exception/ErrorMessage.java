package ru.practicum.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorMessage {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}
