package com.umc.yeogi_gal_lae.api.place.repository;

import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllByTripPlanId(Long tripPlanId);

    List<Place> findByTripPlan(TripPlan tripPlan);
}
