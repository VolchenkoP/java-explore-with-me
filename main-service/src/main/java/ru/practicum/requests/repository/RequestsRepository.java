package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.requests.dto.EventIdByRequestsCount;
import ru.practicum.requests.model.Requests;

import java.util.Collection;
import java.util.List;

public interface RequestsRepository extends JpaRepository<Requests, Long> {

    Collection<Requests> findByEventId(long eventId);

    List<Requests> findByRequesterId(long requesterId);

    int countByEventIdAndStatus(long eventId, String requestState);

    @Query(value = "select count(id), event " +
            "from requests " +
            "where event IN ?1 " +
            "AND status LIKE ?2 " +
            "group by event ", nativeQuery = true)
    List<EventIdByRequestsCount> countByEventIdInAndStatusGroupByEvent(List<Long> eventId, String requestState);

    List<Requests> findByIdInAndEventId(List<Long> id, long eventId);
}
