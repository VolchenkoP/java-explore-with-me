package ru.practicum.compilations.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompositeKeyForEventByCompilation implements Serializable {

    private Integer compilationId;
    private Long eventId;
}
