package ru.practicum.events.model;

import lombok.Getter;

@Getter
public enum EventStates {
    PENDING(1),
    PUBLISHED(2),
    CANCELED(3);

    private final int state;

    EventStates(int state) {
        this.state = state;
    }
}
