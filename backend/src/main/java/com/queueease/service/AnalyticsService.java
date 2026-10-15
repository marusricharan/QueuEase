package com.queueease.service;

import com.queueease.dto.DailySummaryDTO;
import com.queueease.entity.BookingStatus;
import com.queueease.entity.Business;
import com.queueease.entity.QueueEntry;
import com.queueease.entity.QueueStatus;
import com.queueease.repository.BookingRepository;
import com.queueease.repository.BusinessRepository;
import com.queueease.repository.QueueEntryRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final QueueEntryRepository queueEntryRepository;
    private final BookingRepository bookingRepository;
    private final BusinessRepository businessRepository;

    public AnalyticsService(QueueEntryRepository queueEntryRepository,
                            BookingRepository bookingRepository,
                            BusinessRepository businessRepository) {
        this.queueEntryRepository = queueEntryRepository;
        this.bookingRepository = bookingRepository;
        this.businessRepository = businessRepository;
    }

    public DailySummaryDTO getDailySummary(Long businessId, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<QueueEntry> entries = queueEntryRepository.findDailyEntries(businessId, startOfDay, endOfDay);

        long served = 0;
        long cancelled = 0;
        long noShow = 0;
        long waiting = 0;
        long inService = 0;

        long totalWaitMinutes = 0;
        long waitCount = 0;

        Map<Integer, Integer> hourlyMap = new HashMap<>();
        for (int h = 9; h <= 18; h++) {
            hourlyMap.put(h, 0);
        }

        for (QueueEntry e : entries) {
            if (e.getStatus() == QueueStatus.COMPLETED) {
                served++;
                if (e.getServedTime() != null && e.getJoinedTime() != null) {
                    long waitMins = Duration.between(e.getJoinedTime(), e.getServedTime()).toMinutes();
                    totalWaitMinutes += Math.max(0, waitMins);
                    waitCount++;
                }
            } else if (e.getStatus() == QueueStatus.CANCELLED) {
                cancelled++;
            } else if (e.getStatus() == QueueStatus.NO_SHOW) {
                noShow++;
            } else if (e.getStatus() == QueueStatus.WAITING) {
                waiting++;
            } else if (e.getStatus() == QueueStatus.IN_SERVICE || e.getStatus() == QueueStatus.CALLED) {
                inService++;
            }

            // Track arrival hour
            int hour = e.getJoinedTime().getHour();
            hourlyMap.put(hour, hourlyMap.getOrDefault(hour, 0) + 1);
        }

        double avgWait = waitCount > 0 ? (double) totalWaitMinutes / waitCount :
                (business.getAvgServiceDurationMinutes() != null ? business.getAvgServiceDurationMinutes() : 15);

        long totalAppointments = bookingRepository.countByBusinessIdAndBookingDateAndStatus(
                businessId, date, BookingStatus.CONFIRMED);

        DailySummaryDTO summary = new DailySummaryDTO();
        summary.setBusinessId(businessId);
        summary.setBusinessName(business.getName());
        summary.setDate(date);
        summary.setTotalServed(served);
        summary.setTotalCancelled(cancelled);
        summary.setTotalNoShow(noShow);
        summary.setTotalWaiting(waiting);
        summary.setTotalInService(inService);
        summary.setTotalAppointments(totalAppointments);
        summary.setAvgWaitTimeMinutes(Math.round(avgWait * 10.0) / 10.0);

        List<DailySummaryDTO.HourlyStatDTO> hourlyList = new ArrayList<>();
        for (int h = 9; h <= 18; h++) {
            String label = String.format("%02d:00", h);
            hourlyList.add(new DailySummaryDTO.HourlyStatDTO(label, hourlyMap.getOrDefault(h, 0)));
        }
        summary.setHourlyTraffic(hourlyList);

        return summary;
    }
}
