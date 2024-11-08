package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.StatisticResponse;
import ru.practicum.model.Statistics;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    @Query("SELECT new ru.practicum.StatisticResponse(st.app, st.uri, COUNT(st.ip)) " +
            "FROM Statistics AS st " +
            "WHERE st.uri IN :uriList " +
            "AND st.timestamp BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC ")
    List<StatisticResponse> findByUriInAndStartBetween(@Param("uriList") Iterable<String> uri,
                                                       @Param("startDateTime") LocalDateTime start,
                                                       @Param("endDateTime") LocalDateTime end);

    @Query("SELECT new ru.practicum.StatisticResponse(st.app, st.uri, COUNT(DISTINCT st.ip)) " +
            "FROM Statistics AS st " +
            "WHERE st.uri IN :uriList " +
            "AND st.timestamp BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC ")
    List<StatisticResponse> findByUriInAndStartBetweenUniqueIp(@Param("uriList") Iterable<String> uri,
                                                               @Param("startDateTime") LocalDateTime start,
                                                               @Param("endDateTime") LocalDateTime end);

    @Query("SELECT new ru.practicum.StatisticResponse(st.app, st.uri, COUNT(DISTINCT st.ip)) " +
            "FROM Statistics AS st " +
            "WHERE st.timestamp BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC ")
    List<StatisticResponse> findStartBetween(@Param("startDateTime") LocalDateTime start,
                                             @Param("endDateTime") LocalDateTime end);

    @Query("SELECT new ru.practicum.StatisticResponse(st.app, st.uri, COUNT(st.ip)) " +
            "FROM Statistics AS st " +
            "WHERE st.timestamp BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC ")
    List<StatisticResponse> findStartBetweenUniqueIp(@Param("startDateTime") LocalDateTime start,
                                                     @Param("endDateTime") LocalDateTime end);

}
