package com.queueease.service;

import com.queueease.dto.BookingDTO;
import com.queueease.dto.CreateBookingRequest;
import com.queueease.dto.UpdateBookingStatusRequest;
import com.queueease.entity.*;
import com.queueease.repository.BookingRepository;
import com.queueease.repository.BusinessRepository;
import com.queueease.repository.ServiceRepository;
import com.queueease.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    public BookingService(BookingRepository bookingRepository,
                          BusinessRepository businessRepository,
                          ServiceRepository serviceRepository,
                          StatusHistoryRepository statusHistoryRepository) {
        this.bookingRepository = bookingRepository;
        this.businessRepository = businessRepository;
        this.serviceRepository = serviceRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    @Transactional
    public BookingDTO createBooking(User customer, CreateBookingRequest request) {
        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        if (!business.isActive()) {
            throw new IllegalStateException("Business is currently inactive.");
        }

        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        // Safeguard: Booking date cannot be in the past
        if (request.getBookingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book appointments for past dates.");
        }

        // Calculate end time based on service duration
        int duration = service.getDurationMinutes() != null ? service.getDurationMinutes() : 30;
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = startTime.plusMinutes(duration);

        // Safeguard: Must be within business operating hours
        if (business.getOpenTime() != null && startTime.isBefore(business.getOpenTime())) {
            throw new IllegalArgumentException("Requested time is before business opening hours (" + business.getOpenTime() + ")");
        }
        if (business.getCloseTime() != null && endTime.isAfter(business.getCloseTime())) {
            throw new IllegalArgumentException("Service would end after business closing hours (" + business.getCloseTime() + ")");
        }

        // Safeguard: Check for conflicting bookings
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                business.getId(), request.getBookingDate(), startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("This time slot is already booked. Please select another time.");
        }

        Booking booking = new Booking(
                business,
                customer,
                service,
                request.getBookingDate(),
                startTime,
                endTime,
                request.getNotes()
        );
        booking.setStatus(BookingStatus.CONFIRMED); // Auto-confirmed in prototype
        booking = bookingRepository.save(booking);

        StatusHistory history = new StatusHistory(
                null, booking.getId(), null, booking.getStatus().name(),
                customer.getName(), "Appointment booked for " + service.getName()
        );
        statusHistoryRepository.save(history);

        return mapToDTO(booking);
    }

    @Transactional
    public BookingDTO updateStatus(Long bookingId, UpdateBookingStatusRequest request, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        String oldStatus = booking.getStatus().name();
        booking.setStatus(request.getStatus());
        if (request.getNotes() != null) {
            booking.setNotes(request.getNotes());
        }
        booking = bookingRepository.save(booking);

        StatusHistory history = new StatusHistory(
                null, booking.getId(), oldStatus, request.getStatus().name(),
                user.getName(), request.getNotes() != null ? request.getNotes() : "Status updated"
        );
        statusHistoryRepository.save(history);

        return mapToDTO(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, User customer) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customer.getId()) && customer.getRole() != Role.ROLE_ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Cannot cancel another user's booking.");
        }

        String oldStatus = booking.getStatus().name();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        StatusHistory history = new StatusHistory(
                null, booking.getId(), oldStatus, BookingStatus.CANCELLED.name(),
                customer.getName(), "Customer cancelled appointment"
        );
        statusHistoryRepository.save(history);
    }

    public List<BookingDTO> getCustomerBookings(User customer) {
        return bookingRepository.findByCustomerOrderByBookingDateDescStartTimeAsc(customer).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBusinessBookings(Long businessId) {
        return bookingRepository.findByBusinessIdOrderByBookingDateDescStartTimeAsc(businessId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setBusinessId(booking.getBusiness().getId());
        dto.setBusinessName(booking.getBusiness().getName());
        dto.setBusinessCategory(booking.getBusiness().getCategory());
        dto.setBusinessAddress(booking.getBusiness().getAddress() + ", " + booking.getBusiness().getCity());

        if (booking.getCustomer() != null) {
            dto.setCustomerId(booking.getCustomer().getId());
            dto.setCustomerName(booking.getCustomer().getName());
        }

        if (booking.getService() != null) {
            dto.setServiceId(booking.getService().getId());
            dto.setServiceName(booking.getService().getName());
            dto.setServiceDuration(booking.getService().getDurationMinutes());
            dto.setServicePrice(booking.getService().getPrice());
        }

        dto.setBookingDate(booking.getBookingDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setNotes(booking.getNotes());
        dto.setCreatedAt(booking.getCreatedAt());

        return dto;
    }
}
