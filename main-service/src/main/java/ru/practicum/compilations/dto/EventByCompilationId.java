package ru.practicum.compilations.dto;

import ru.practicum.events.model.Event;

public interface EventByCompilationId {
    Integer getCompilationId();

    Event getEvent();
}
