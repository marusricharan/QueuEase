package com.queueease.service;

import com.queueease.dto.JoinQueueRequest;
import com.queueease.dto.QueueEntryDTO;
import com.queueease.dto.UpdateQueueStatusRequest;
import com.queueease.entity.*;
import com.queueease.repository.BusinessRepository;
import com.queueease.repository.QueueEntryRepository;
import com.queueease.repository.ServiceRepository;
import com.queueease.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final QueueEntryRepository queueEntryRepository;
    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    public QueueService(QueueEntryRepository queueEntryRepository,
                        BusinessRepository businessRepository,
                        ServiceRepository serviceRepository,
                        StatusHistoryRepository statusHistoryRepository) {
        this.queueEntryRepository = queueEntryRepository;
        this.businessRepository = businessRepository;
        this.serviceRepository = serviceRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    /**
     * Customer or walk-in joins the virtual queue.
     */
    @Transactional
    public QueueEntryDTO joinQueue(User customer, JoinQueueRequest request) {
        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        if (!business.isActive()) {
            throw new IllegalStateException("This business is currently inactive.");
        }

        if (!business.isQueueOpen()) {
            throw new IllegalStateException("The live queue is currently paused or closed for this business.");
        }

        // Safeguard: Check if customer already has an active entry in this business's queue
        if (customer != null) {
            Optional<QueueEntry> activeEntry = queueEntryRepository.findByBusinessAndCustomerAndStatusIn(
                    business, customer, List.of(QueueStatus.WAITING, QueueStatus.CALLED, QueueStatus.IN_SERVICE));
            if (activeEntry.isPresent()) {
                throw new IllegalStateException("You already have an active ticket (" +
                        activeEntry.get().getTicketNumber() + ") in this queue!");
            }
        }

        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        // Safeguard: Daily capacity limit
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        List<QueueEntry> todayEntries = queueEntryRepository.findDailyEntries(business.getId(), startOfDay, endOfDay);
        if (todayEntries.size() >= business.getDailyCapacity()) {
            throw new IllegalStateException("Daily capacity reached for this business today.");
        }

        // Generate Ticket Number (e.g. Q-101, Q-102...)
        int ticketSequence = todayEntries.size() + 101;
        String prefix = business.getName().substring(0, 1).toUpperCase();
        String ticketNumber = prefix + "-" + ticketSequence;

        String customerName = customer != null ? customer.getName() : request.getCustomerName();
        String customerPhone = customer != null ? customer.getPhone() : request.getCustomerPhone();

        QueueEntry entry = new QueueEntry(business, customer, customerName, customerPhone, service, ticketNumber);
        entry.setNotes(request.getNotes());
        entry = queueEntryRepository.save(entry);

        // Record Initial History
        recordStatusHistory(entry.getId(), null, null, QueueStatus.WAITING.name(),
                customerName, "Joined live queue for " + service.getName());

        // Recalculate wait time for all waiting customers in this business
        recalculateQueueWaitEstimates(business.getId());

        return mapToDTO(entry);
    }

    /**
     * Recalculates estimated wait for all WAITING tickets in a business.
     * Standout Feature: Estimated wait = people ahead * average service duration
     */
    @Transactional
    public void recalculateQueueWaitEstimates(Long businessId) {
        Business business = businessRepository.findById(businessId).orElse(null);
        if (business == null) return;

        int avgDuration = (business.getAvgServiceDurationMinutes() != null && business.getAvgServiceDurationMinutes() > 0)
                ? business.getAvgServiceDurationMinutes() : 20;

        // Check if there is someone currently IN_SERVICE
        List<QueueEntry> inServiceList = queueEntryRepository.findByBusinessIdAndStatusInOrderByJoinedTimeAsc(
                businessId, List.of(QueueStatus.IN_SERVICE));

        int remainingInServiceMinutes = 0;
        if (!inServiceList.isEmpty()) {
            QueueEntry currentInService = inServiceList.get(0);
            if (currentInService.getServedTime() != null) {
                long elapsed = Duration.between(currentInService.getServedTime(), LocalDateTime.now()).toMinutes();
                remainingInServiceMinutes = (int) Math.max(0, avgDuration - elapsed);
            } else {
                remainingInServiceMinutes = avgDuration / 2;
            }
        }

        // Fetch all waiting tickets sorted by joinedTime ascending
        List<QueueEntry> waitingList = queueEntryRepository.findByBusinessIdAndStatusInOrderByJoinedTimeAsc(
                businessId, List.of(QueueStatus.WAITING));

        for (int i = 0; i < waitingList.size(); i++) {
            QueueEntry waitingEntry = waitingList.get(i);
            // people ahead = i
            int estimatedWait = (i * avgDuration) + remainingInServiceMinutes;
            waitingEntry.setEstimatedWaitMinutes(estimatedWait);
            queueEntryRepository.save(waitingEntry);
        }
    }

    /**
     * Staff calls the next waiting customer.
     */
    @Transactional
    public QueueEntryDTO callNext(Long businessId, Integer counterNumber, User staffUser) {
        List<QueueEntry> waitingList = queueEntryRepository.findByBusinessIdAndStatusInOrderByJoinedTimeAsc(
                businessId, List.of(QueueStatus.WAITING));

        if (waitingList.isEmpty()) {
            throw new IllegalStateException("No customers are currently waiting in the queue.");
        }

        QueueEntry next = waitingList.get(0);
        String oldStatus = next.getStatus().name();
        next.setStatus(QueueStatus.CALLED);
        next.setCounterNumber(counterNumber != null ? counterNumber : 1);
        next.setCalledTime(LocalDateTime.now());
        next.setEstimatedWaitMinutes(0);

        next = queueEntryRepository.save(next);

        recordStatusHistory(next.getId(), null, oldStatus, QueueStatus.CALLED.name(),
                staffUser.getName(), "Called to counter " + next.getCounterNumber());

        recalculateQueueWaitEstimates(businessId);
        return mapToDTO(next);
    }

    /**
     * Staff updates queue status (e.g. IN_SERVICE, COMPLETED, CANCELLED, NO_SHOW).
     */
    @Transactional
    public QueueEntryDTO updateStatus(Long queueEntryId, UpdateQueueStatusRequest request, User staffUser) {
        QueueEntry entry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueEntryId));

        QueueStatus oldStatus = entry.getStatus();
        QueueStatus newStatus = request.getStatus();

        entry.setStatus(newStatus);
        if (request.getCounterNumber() != null) {
            entry.setCounterNumber(request.getCounterNumber());
        }
        if (request.getNotes() != null) {
            entry.setNotes(request.getNotes());
        }

        LocalDateTime now = LocalDateTime.now();
        if (newStatus == QueueStatus.CALLED) {
            entry.setCalledTime(now);
            entry.setEstimatedWaitMinutes(0);
        } else if (newStatus == QueueStatus.IN_SERVICE) {
            entry.setServedTime(now);
            entry.setEstimatedWaitMinutes(0);
        } else if (newStatus == QueueStatus.COMPLETED) {
            entry.setCompletedTime(now);
            entry.setEstimatedWaitMinutes(0);
        } else if (newStatus == QueueStatus.CANCELLED || newStatus == QueueStatus.NO_SHOW) {
            entry.setEstimatedWaitMinutes(0);
        }

        entry = queueEntryRepository.save(entry);

        recordStatusHistory(entry.getId(), null, oldStatus.name(), newStatus.name(),
                staffUser != null ? staffUser.getName() : "System",
                request.getNotes() != null ? request.getNotes() : "Status updated to " + newStatus);

        // Automatically trigger wait time recalculation for remaining customers
        recalculateQueueWaitEstimates(entry.getBusiness().getId());

        return mapToDTO(entry);
    }

    /**
     * Customer leaves the queue.
     */
    @Transactional
    public void leaveQueue(Long queueEntryId, User customer) {
        QueueEntry entry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found"));

        if (customer != null && entry.getCustomer() != null && !entry.getCustomer().getId().equals(customer.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You cannot cancel someone else's queue ticket.");
        }

        String oldStatus = entry.getStatus().name();
        entry.setStatus(QueueStatus.CANCELLED);
        entry.setEstimatedWaitMinutes(0);
        queueEntryRepository.save(entry);

        recordStatusHistory(entry.getId(), null, oldStatus, QueueStatus.CANCELLED.name(),
                customer != null ? customer.getName() : "Customer", "Customer cancelled / left the queue.");

        recalculateQueueWaitEstimates(entry.getBusiness().getId());
    }

    public List<QueueEntryDTO> getBusinessQueue(Long businessId) {
        List<QueueEntry> entries = queueEntryRepository.findByBusinessIdAndStatusInOrderByJoinedTimeAsc(
                businessId, List.of(QueueStatus.IN_SERVICE, QueueStatus.CALLED, QueueStatus.WAITING));
        return entries.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<QueueEntryDTO> getBusinessCompletedToday(Long businessId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        List<QueueEntry> entries = queueEntryRepository.findDailyEntries(businessId, startOfDay, endOfDay);
        return entries.stream()
                .filter(e -> e.getStatus() == QueueStatus.COMPLETED || e.getStatus() == QueueStatus.CANCELLED || e.getStatus() == QueueStatus.NO_SHOW)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<QueueEntryDTO> getCustomerActiveEntry(Long businessId, User customer) {
        return queueEntryRepository.findByBusinessIdAndCustomerAndStatusIn(
                businessId, customer, List.of(QueueStatus.WAITING, QueueStatus.CALLED, QueueStatus.IN_SERVICE))
                .map(this::mapToDTO);
    }

    public List<QueueEntryDTO> getCustomerQueueHistory(User customer) {
        return queueEntryRepository.findByCustomerOrderByJoinedTimeDesc(customer).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void recordStatusHistory(Long queueId, Long bookingId, String oldStatus, String newStatus, String changedBy, String notes) {
        StatusHistory history = new StatusHistory(queueId, bookingId, oldStatus, newStatus, changedBy, notes);
        statusHistoryRepository.save(history);
    }

    public QueueEntryDTO mapToDTO(QueueEntry entry) {
        QueueEntryDTO dto = new QueueEntryDTO();
        dto.setId(entry.getId());
        dto.setBusinessId(entry.getBusiness().getId());
        dto.setBusinessName(entry.getBusiness().getName());
        dto.setBusinessCategory(entry.getBusiness().getCategory());

        if (entry.getCustomer() != null) {
            dto.setCustomerId(entry.getCustomer().getId());
        }
        dto.setCustomerName(entry.getCustomerName());
        dto.setCustomerPhone(entry.getCustomerPhone());

        if (entry.getService() != null) {
            dto.setServiceId(entry.getService().getId());
            dto.setServiceName(entry.getService().getName());
            dto.setServiceDuration(entry.getService().getDurationMinutes());
            dto.setServicePrice(entry.getService().getPrice());
        }

        dto.setTicketNumber(entry.getTicketNumber());
        dto.setStatus(entry.getStatus());
        dto.setCounterNumber(entry.getCounterNumber());
        dto.setJoinedTime(entry.getJoinedTime());
        dto.setCalledTime(entry.getCalledTime());
        dto.setServedTime(entry.getServedTime());
        dto.setCompletedTime(entry.getCompletedTime());
        dto.setEstimatedWaitMinutes(entry.getEstimatedWaitMinutes());
        dto.setNotes(entry.getNotes());

        if (entry.getStatus() == QueueStatus.WAITING) {
            long peopleAhead = queueEntryRepository.countAheadInQueue(entry.getBusiness().getId(), entry.getJoinedTime());
            dto.setPeopleAhead(peopleAhead);
        } else {
            dto.setPeopleAhead(0);
        }

        return dto;
    }
}
