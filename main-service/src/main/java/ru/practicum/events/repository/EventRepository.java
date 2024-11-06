package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByIdIn(List<Long> id);

    List<Event> findByInitiatorId(long userId, Pageable pageable);

    List<Event> findByInitiatorIdAndState(long userId, String state, Pageable pageable);

    Optional<Event> findByIdAndState(long id, String state);

    @Query(value = "SELECT * " +
            "FROM events AS e " +
            "WHERE ((e.state IN (:state) OR :state IS NULL) " +
            "AND (e.category IN (:category) OR :category IS NULL) " +
            "AND (e.initiator IN (:initiator) OR :initiator IS NULL) " +
            "AND (e.event_date BETWEEN :rangeStart AND :rangeEnd)) ", nativeQuery = true)
    List<Event> findByConditionals(@Param("state") List<String> state,
                                   @Param("category") List<Integer> category,
                                   @Param("initiator") List<Long> initiator,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM events AS e " +
            "WHERE (((e.annotation ILIKE %?1% OR e.description ILIKE %?1%) OR ?1 IS NULL) " +
            "AND (e.category IN (?2) OR ?2 IS NULL) " +
            "AND (e.paid = CAST(?3 AS boolean) OR ?3 IS NULL) " +
            "AND (e.event_date BETWEEN ?4 AND ?5 ) " +
            "AND (CAST(?6 AS BOOLEAN) is TRUE " +
            "  OR( " +
            "  select count(id) " +
            "  from requests AS r " +
            "  WHERE r.event = e.id) < participants_limit) " +
            "AND state = 'PUBLISHED') ",
            nativeQuery = true)
    List<Event> searchEvents(String text, List<Integer> category, Boolean paid, LocalDateTime rangStart,
                             LocalDateTime rangeEnd, boolean isAvailable, Pageable pageable);
}

