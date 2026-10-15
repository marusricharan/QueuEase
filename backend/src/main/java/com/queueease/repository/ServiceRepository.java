package com.queueease.repository;

import com.queueease.entity.Business;
import com.queueease.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByBusinessAndActiveTrue(Business business);
    List<ServiceEntity> findByBusinessIdAndActiveTrue(Long businessId);
}
