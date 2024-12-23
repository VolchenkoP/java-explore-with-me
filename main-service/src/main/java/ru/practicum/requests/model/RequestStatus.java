package ru.practicum.requests.model;

import lombok.Getter;

@Getter
public enum RequestStatus {
    PENDING(1),
    CONFIRMED(2),
    REJECTED(3),
    CANCELED(4);

    private final int requestStatus;

    RequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }
}
