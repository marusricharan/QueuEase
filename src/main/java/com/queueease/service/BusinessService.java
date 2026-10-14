package com.queueease.service;

import com.queueease.dto.BusinessDTO;
import com.queueease.dto.ServiceDTO;
import com.queueease.entity.*;
import com.queueease.repository.BusinessRepository;
import com.queueease.repository.QueueEntryRepository;
import com.queueease.repository.ReviewRepository;
import com.queueease.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final ReviewRepository reviewRepository;

    public BusinessService(BusinessRepository businessRepository,
                           ServiceRepository serviceRepository,
                           QueueEntryRepository queueEntryRepository,
                           ReviewRepository reviewRepository) {
        this.businessRepository = businessRepository;
        this.serviceRepository = serviceRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<BusinessDTO> getAllActiveBusinesses() {
        return businessRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BusinessDTO> searchBusinesses(String query) {
        if (query == null || query.isBlank()) {
            return getAllActiveBusinesses();
        }
        return businessRepository.searchBusinesses(query.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BusinessDTO> getBusinessesByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("ALL")) {
            return getAllActiveBusinesses();
        }
        return businessRepository.findByCategoryIgnoreCaseAndActiveTrue(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BusinessDTO getBusinessById(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Business not found with id: " + id));
        return mapToDTO(business);
    }

    public Business getBusinessEntity(Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Business not found with id: " + id));
    }

    @Transactional
    public BusinessDTO updateBusinessSettings(Long id, BusinessDTO dto, User staffUser) {
        Business business = getBusinessEntity(id);
        if (staffUser.getRole() != Role.ROLE_ADMIN && !business.getOwner().getId().equals(staffUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to manage this business.");
        }

        if (dto.getName() != null) business.setName(dto.getName());
        if (dto.getDescription() != null) business.setDescription(dto.getDescription());
        if (dto.getCategory() != null) business.setCategory(dto.getCategory());
        if (dto.getAddress() != null) business.setAddress(dto.getAddress());
        if (dto.getCity() != null) business.setCity(dto.getCity());
        if (dto.getPhone() != null) business.setPhone(dto.getPhone());
        if (dto.getOpenTime() != null) business.setOpenTime(dto.getOpenTime());
        if (dto.getCloseTime() != null) business.setCloseTime(dto.getCloseTime());
        if (dto.getAvgServiceDurationMinutes() != null && dto.getAvgServiceDurationMinutes() > 0) {
            business.setAvgServiceDurationMinutes(dto.getAvgServiceDurationMinutes());
        }
        if (dto.getDailyCapacity() != null && dto.getDailyCapacity() > 0) {
            business.setDailyCapacity(dto.getDailyCapacity());
        }
        business.setQueueOpen(dto.isQueueOpen());

        business = businessRepository.save(business);
        return mapToDTO(business);
    }

    @Transactional
    public ServiceDTO addService(Long businessId, ServiceDTO serviceDTO, User staffUser) {
        Business business = getBusinessEntity(businessId);
        if (staffUser.getRole() != Role.ROLE_ADMIN && !business.getOwner().getId().equals(staffUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }

        ServiceEntity service = new ServiceEntity(
                business,
                serviceDTO.getName(),
                serviceDTO.getDescription(),
                serviceDTO.getDurationMinutes() != null ? serviceDTO.getDurationMinutes() : 20,
                serviceDTO.getPrice()
        );
        service = serviceRepository.save(service);
        return mapServiceToDTO(service);
    }

    @Transactional
    public ServiceDTO updateService(Long serviceId, ServiceDTO serviceDTO, User staffUser) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        Business business = service.getBusiness();
        if (staffUser.getRole() != Role.ROLE_ADMIN && !business.getOwner().getId().equals(staffUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }

        if (serviceDTO.getName() != null) service.setName(serviceDTO.getName());
        if (serviceDTO.getDescription() != null) service.setDescription(serviceDTO.getDescription());
        if (serviceDTO.getDurationMinutes() != null) service.setDurationMinutes(serviceDTO.getDurationMinutes());
        if (serviceDTO.getPrice() != null) service.setPrice(serviceDTO.getPrice());
        service.setActive(serviceDTO.isActive());

        service = serviceRepository.save(service);
        return mapServiceToDTO(service);
    }

    @Transactional
    public void deleteService(Long serviceId, User staffUser) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        Business business = service.getBusiness();
        if (staffUser.getRole() != Role.ROLE_ADMIN && !business.getOwner().getId().equals(staffUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
        service.setActive(false);
        serviceRepository.save(service);
    }

    public BusinessDTO mapToDTO(Business business) {
        BusinessDTO dto = new BusinessDTO();
        dto.setId(business.getId());
        dto.setName(business.getName());
        dto.setCategory(business.getCategory());
        dto.setDescription(business.getDescription());
        dto.setAddress(business.getAddress());
        dto.setCity(business.getCity());
        dto.setPhone(business.getPhone());
        dto.setOpeningHours(business.getOpeningHours());
        dto.setOpenTime(business.getOpenTime());
        dto.setCloseTime(business.getCloseTime());
        dto.setAvgServiceDurationMinutes(business.getAvgServiceDurationMinutes());
        dto.setDailyCapacity(business.getDailyCapacity());
        dto.setActive(business.isActive());
        dto.setQueueOpen(business.isQueueOpen());

        if (business.getOwner() != null) {
            dto.setOwnerId(business.getOwner().getId());
            dto.setOwnerName(business.getOwner().getName());
        }

        // Live Queue Metrics
        List<QueueEntry> activeWaiting = queueEntryRepository.findByBusinessAndStatusInOrderByJoinedTimeAsc(
                business, List.of(QueueStatus.WAITING));
        List<QueueEntry> inService = queueEntryRepository.findByBusinessAndStatusInOrderByJoinedTimeAsc(
                business, List.of(QueueStatus.IN_SERVICE, QueueStatus.CALLED));

        int waitingCount = activeWaiting.size();
        int inServiceCount = inService.size();

        // Estimated Wait: waitingCount * avgDuration (minimum 0)
        int waitTime = waitingCount * (business.getAvgServiceDurationMinutes() != null ? business.getAvgServiceDurationMinutes() : 20);
        if (inServiceCount > 0 && waitTime == 0) {
            // If someone is being served and 0 people are waiting, wait is half the service time
            waitTime = (business.getAvgServiceDurationMinutes() != null ? business.getAvgServiceDurationMinutes() : 20) / 2;
        }

        dto.setWaitingCount(waitingCount);
        dto.setInServiceCount(inServiceCount);
        dto.setCurrentWaitMinutes(waitTime);

        // Reviews
        List<Review> reviews = reviewRepository.findByBusinessIdOrderByCreatedAtDesc(business.getId());
        dto.setReviewCount(reviews.size());
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(5.0);
            dto.setAverageRating(Math.round(avgRating * 10.0) / 10.0);
        } else {
            dto.setAverageRating(5.0);
        }

        // Services
        List<ServiceEntity> services = serviceRepository.findByBusinessAndActiveTrue(business);
        dto.setServices(services.stream().map(this::mapServiceToDTO).collect(Collectors.toList()));

        return dto;
    }

    public ServiceDTO mapServiceToDTO(ServiceEntity service) {
        return new ServiceDTO(
                service.getId(),
                service.getBusiness().getId(),
                service.getName(),
                service.getDescription(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.isActive()
        );
    }
}
