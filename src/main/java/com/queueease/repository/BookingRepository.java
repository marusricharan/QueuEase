package com.queueease.repository;

import com.queueease.entity.Booking;
import com.queueease.entity.BookingStatus;
import com.queueease.entity.Business;
import com.queueease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBusinessOrderByBookingDateDescStartTimeAsc(Business business);
    List<Booking> findByBusinessIdOrderByBookingDateDescStartTimeAsc(Long businessId);
    List<Booking> findByCustomerOrderByBookingDateDescStartTimeAsc(User customer);
    List<Booking> findByBusinessIdAndBookingDate(Long businessId, LocalDate bookingDate);

    @Query("SELECT b FROM Booking b WHERE b.business.id = :businessId " +
           "AND b.bookingDate = :bookingDate " +
           "AND b.status NOT IN ('CANCELLED') " +
           "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookings(@Param("businessId") Long businessId,
                                         @Param("bookingDate") LocalDate bookingDate,
                                         @Param("startTime") LocalTime startTime,
                                         @Param("endTime") LocalTime endTime);

    long countByBusinessIdAndBookingDateAndStatus(Long businessId, LocalDate bookingDate, BookingStatus status);
}
