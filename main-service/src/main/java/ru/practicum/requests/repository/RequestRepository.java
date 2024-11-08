package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.requests.dto.EventIdByRequestsCount;
import ru.practicum.requests.model.Requests;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Requests, Long> {

    Collection<Requests> findByEventId(long eventId);

    List<Requests> findByRequesterId(long requesterId);

    int countByEventIdAndStatus(long eventId, String requestState);

    @Query(value = "select count(id), event " +
            "from requests " +
            "where event IN :eventId " +
            "AND status LIKE :requestState " +
            "group by event", nativeQuery = true)
    List<EventIdByRequestsCount> countByEventIdInAndStatusGroupByEvent(@Param("eventId") List<Long> eventId,
                                                                       @Param("requestState") String requestState);

    List<Requests> findByIdInAndEventId(List<Long> id, long eventId);

}
