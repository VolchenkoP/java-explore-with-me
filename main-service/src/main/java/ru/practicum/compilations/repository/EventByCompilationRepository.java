package ru.practicum.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.compilations.dto.EventByCompilationId;
import ru.practicum.compilations.model.CompositeKeyForEventByCompilation;
import ru.practicum.compilations.model.EventsByCompilation;

import java.util.Collection;
import java.util.List;

public interface EventByCompilationRepository extends JpaRepository<EventsByCompilation,
        CompositeKeyForEventByCompilation> {

    @Query(value = "SELECT event_id " +
            "FROM events_by_compilations " +
            "WHERE compilation_id = ?1 ",
            nativeQuery = true)
    List<Long> findByCompilationId(int compilationId);

    @Query(value = "select compilation_id, e.* " +
            "from events_by_compilations AS ebc " +
            "INNER JOIN events AS e on ebc.event_id = e.id " +
            "where compilation_id IN (?1) ",
            nativeQuery = true)
    List<EventByCompilationId> findEventsByCompilationIdIn(Collection<Integer> compId);

    @Query(value = "delete from events_by_compilations " +
            "where compilation_id = ?1 ",
            nativeQuery = true)
    void deleteByCompilationId(int compilationId);
}
