package ru.practicum.compilations.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events_by_compilations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventsByCompilation {
    @EmbeddedId
    private CompositeKeyForEventByCompilation compositeKey;
}
