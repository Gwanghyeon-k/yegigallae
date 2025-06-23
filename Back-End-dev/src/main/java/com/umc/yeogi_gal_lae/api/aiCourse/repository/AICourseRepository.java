package com.umc.yeogi_gal_lae.api.aiCourse.repository;

import com.umc.yeogi_gal_lae.api.aiCourse.domain.AICourse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AICourseRepository extends JpaRepository<AICourse, Long> {
    @Query("SELECT a FROM AICourse a WHERE a.tripPlan.id = :tripPlanId ORDER BY a.id DESC")
    Optional<AICourse> findLatestByTripPlanId(@Param("tripPlanId") Long tripPlanId);

    // AICourseRepository.java
    List<AICourse> findAllByTripPlanId(Long tripPlanId);


}
