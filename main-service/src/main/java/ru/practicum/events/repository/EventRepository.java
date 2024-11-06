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

    @Query(value = "SELECT * FROM events AS e " +
            "WHERE (((e.annotation ILIKE :text OR e.description ILIKE :text) OR :text IS NULL) " +
            "AND (:category IN :categories OR :category IS NULL) " +
            "AND (e.paid = CAST(:paid AS boolean) OR :paid IS NULL) " +
            "AND (e.event_date BETWEEN :startDate AND :endDate ) " +
            "AND (:isAvailable IS TRUE " +
            "  OR( " +
            "  select count(id) " +
            "  from requests AS r " +
            "  WHERE r.event = e.id) < participantsLimit) " +
            "AND state = 'PUBLISHED' " +
            "ORDER BY e.id LIMIT :size OFFSET :offset",
            nativeQuery = true)
    List<Event> searchEvents(
            @Param("text") String text,
            @Param("categories") List<Integer> categories,
            @Param("paid") Boolean paid,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("isAvailable") boolean isAvailable,
            @Param("size") int size,
            @Param("offset") int startPaige);
}

