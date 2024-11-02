package ru.practicum.events.model;

import lombok.Getter;

@Getter
public enum EventStateAction {
    PUBLISH_EVENT(1),
    REJECT_EVENT(2),
    SEND_TO_REVIEW_EVENT(3),
    CANCEL_REVIEW_EVENT(4);

    private final int stateAction;

    EventStateAction(int stateAction) {
        this.stateAction = stateAction;
    }
}
