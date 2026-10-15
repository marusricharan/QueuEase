package com.queueease.repository;

import com.queueease.entity.Business;
import com.queueease.entity.QueueEntry;
import com.queueease.entity.QueueStatus;
import com.queueease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
    List<QueueEntry> findByBusinessAndStatusInOrderByJoinedTimeAsc(Business business, Collection<QueueStatus> statuses);

    List<QueueEntry> findByBusinessIdAndStatusInOrderByJoinedTimeAsc(Long businessId, Collection<QueueStatus> statuses);

    List<QueueEntry> findByBusinessIdOrderByJoinedTimeDesc(Long businessId);

    List<QueueEntry> findByCustomerOrderByJoinedTimeDesc(User customer);

    Optional<QueueEntry> findByBusinessAndCustomerAndStatusIn(Business business, User customer, Collection<QueueStatus> statuses);

    Optional<QueueEntry> findByBusinessIdAndCustomerAndStatusIn(Long businessId, User customer, Collection<QueueStatus> statuses);

    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.business.id = :businessId AND q.status = 'WAITING' AND q.joinedTime < :joinedTime")
    long countAheadInQueue(@Param("businessId") Long businessId, @Param("joinedTime") LocalDateTime joinedTime);

    @Query("SELECT q FROM QueueEntry q WHERE q.business.id = :businessId AND q.joinedTime >= :startOfDay AND q.joinedTime <= :endOfDay")
    List<QueueEntry> findDailyEntries(@Param("businessId") Long businessId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    long countByBusinessIdAndStatus(Long businessId, QueueStatus status);
}
