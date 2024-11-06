package ru.practicum.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.compilations.dto.EventByCompId;
import ru.practicum.compilations.model.CompositeKeyForEventByComp;
import ru.practicum.compilations.model.EventsByCompilation;

import java.util.Collection;
import java.util.List;

public interface EventByCompilationRepository extends JpaRepository<EventsByCompilation, CompositeKeyForEventByComp> {

    @Query(value = "SELECT event_id " +
            "FROM events_by_compilations " +
            "WHERE compilation_id = :compilationId",
            nativeQuery = true)
    List<Long> findByCompilationId(@Param("compilationId") int compilationId);

    @Query(value = "select compilation_id, e.* " +
            "from events_by_compilations AS ebc " +
            "INNER JOIN events AS e on ebc.event_id = e.id " +
            "where compilation_id IN (:compId)",
            nativeQuery = true)
    List<EventByCompId> findEventsByCompIdIn(@Param("compId") Collection<Integer> compId);

    @Query(value = "delete from events_by_compilations " +
            "where compilation_id = :compilationId",
            nativeQuery = true)
    void deleteByCompilationId(@Param("compilationId") int compilationId);

}
