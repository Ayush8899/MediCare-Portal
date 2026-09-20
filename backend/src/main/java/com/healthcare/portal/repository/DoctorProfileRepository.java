package com.healthcare.portal.repository;

import com.healthcare.portal.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    Optional<DoctorProfile> findByUserId(Long userId);

    @Query("SELECT d FROM DoctorProfile d WHERE " +
           "(:specialization IS NULL OR LOWER(d.specialization) = LOWER(:specialization)) AND " +
           "(:query IS NULL OR LOWER(d.user.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.city) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<DoctorProfile> searchDoctors(@Param("specialization") String specialization, @Param("query") String query);

    @Query("SELECT DISTINCT d.specialization FROM DoctorProfile d")
    List<String> findDistinctSpecializations();
}
