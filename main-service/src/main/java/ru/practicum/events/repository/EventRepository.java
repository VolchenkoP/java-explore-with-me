package ru.practicum.events.repository;

import org.springframework.data.domain.Page;
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
            "WHERE (((e.annotation ILIKE :text OR e.description ILIKE :text) OR :text IS NULL) " +
            "AND (e.category IN (:category) OR :category IS NULL) " +
            "AND (e.paid = CAST(:paid AS boolean) OR :paid IS NULL) " +
            "AND (e.event_date BETWEEN :rangStart AND :rangeEnd) " +
            "AND (:isAvailable is TRUE " +
            "  OR( " +
            "  select count(id) " +
            "  from requests AS r " +
            "  WHERE r.event = e.id) < participants_limit) " +
            "AND state = 'PUBLISHED') " +
            "ORDER BY e.event_date DESC " +
            "LIMIT :limit OFFSET :offset",
            countQuery = "SELECT COUNT(*) FROM events AS e WHERE (((e.annotation ILIKE :text OR e.description ILIKE :text) OR :text IS NULL) " +
                    "AND (e.category IN (:category) OR :category IS NULL) " +
                    "AND (e.paid = CAST(:paid AS boolean) OR :paid IS NULL) " +
                    "AND (e.event_date BETWEEN :rangStart AND :rangeEnd) " +
                    "AND (:isAvailable is TRUE " +
                    "  OR( " +
                    "  select count(id) " +
                    "  from requests AS r " +
                    "  WHERE r.event = e.id) < participants_limit) " +
                    "AND state = 'PUBLISHED')",
            nativeQuery = true)
    Page<Event> searchEvents(@Param("text") String text, @Param("category") List<Integer> category,
                             @Param("paid") Boolean paid, @Param("rangStart") LocalDateTime rangStart,
                             @Param("rangeEnd") LocalDateTime rangeEnd, @Param("isAvailable") boolean isAvailable,
                             Pageable pageable);
}

