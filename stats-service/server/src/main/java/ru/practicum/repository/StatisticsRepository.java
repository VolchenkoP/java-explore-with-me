package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.StatisticsResponse;
import ru.practicum.model.Statistics;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    @Query("SELECT new ru.practicum.dto.StatisticsResponse(st.app, st.uri, COUNT(st.ip)) " +
            "FROM Statistics AS st " +
            "WHERE st.uri IN ?1 " +
            "AND st.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC ")
    List<StatisticsResponse> findByUriInAndStartBetween(Iterable<String> uri, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatisticsResponse(st.app, st.uri, COUNT(DISTINCT st.ip)) " +
            "FROM Statistics AS st " +
            "WHERE st.uri IN ?1 " +
            "AND st.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC ")
    List<StatisticsResponse> findByUriInAndStartBetweenUniqueIp(Iterable<String> uri, LocalDateTime start,
                                                               LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatisticsResponse(st.app, st.uri, COUNT(DISTINCT st.ip)) " +
            "FROM Statistics AS st " +
            "WHERE st.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC ")
    List<StatisticsResponse> findStartBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatisticsResponse(st.app, st.uri, COUNT(st.ip)) " +
            "FROM Statistics AS st " +
            "WHERE st.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC ")
    List<StatisticsResponse> findStartBetweenUniqueIp(LocalDateTime start, LocalDateTime end);

}
