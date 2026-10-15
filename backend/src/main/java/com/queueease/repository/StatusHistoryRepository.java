package com.queueease.repository;

import com.queueease.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByQueueEntryIdOrderByTimestampDesc(Long queueEntryId);
    List<StatusHistory> findByBookingIdOrderByTimestampDesc(Long bookingId);
}
