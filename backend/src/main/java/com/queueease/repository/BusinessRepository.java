package com.queueease.repository;

import com.queueease.entity.Business;
import com.queueease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findByActiveTrue();
    List<Business> findByCategoryIgnoreCaseAndActiveTrue(String category);
    List<Business> findByOwner(User owner);

    @Query("SELECT b FROM Business b WHERE b.active = true AND " +
           "(LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(b.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(b.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(b.address) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Business> searchBusinesses(@Param("query") String query);
}
