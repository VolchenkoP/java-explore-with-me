package ru.practicum.subscriptions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.subscriptions.model.Subscription;
import ru.practicum.subscriptions.model.UserProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserIdAndFollowerId(long userId, long followerId);

    @Query(value = "SELECT u.* " +
            "FROM subscriptions AS s " +
            "INNER JOIN users AS u ON s.user_id = u.id " +
            "WHERE follower = :followerId",
            nativeQuery = true)
    List<UserProjection> findByFollowerId(@Param("followerId") long followerId);

    @Query(value = "SELECT u.* " +
            "FROM subscriptions AS s " +
            "INNER JOIN users AS u ON s.follower = u.id " +
            "WHERE user_id = :userId",
            nativeQuery = true)
    List<UserProjection> findByUserId(@Param("userId") long userId);
}
