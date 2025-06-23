package com.umc.yeogi_gal_lae.api.place.service;

import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.request.PlaceRequest;
import com.umc.yeogi_gal_lae.api.place.repository.PlaceRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final TripPlanRepository tripPlanRepository; // TripPlan 조회용
    private final UserRepository userRepository;  // 사용자 조회용

    @Transactional
    public List<Place> addPlaces(Long tripPlanId, List<PlaceRequest> placeRequests) {
        TripPlan tripPlan = tripPlanRepository.findById(tripPlanId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRIP_PLAN_NOT_FOUND));
        List<Place> savedPlaces = new ArrayList<>();
        for (PlaceRequest request : placeRequests) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            Place place = PlaceConverter.toPlaceEntity(tripPlan, request, user);
            placeRepository.save(place);
            savedPlaces.add(place);
        }
        return savedPlaces;
    }

    public List<Place> getAllPlacesByTripPlanId(Long tripPlanId) {
        if (!tripPlanRepository.existsById(tripPlanId)) {
            throw new BusinessException(ErrorCode.TRIP_PLAN_NOT_FOUND);
        }
        return placeRepository.findAllByTripPlanId(tripPlanId);
    }

    public Place getPlaceById(Long tripPlanId, Long placeId) {
        if (!tripPlanRepository.existsById(tripPlanId)) {
            throw new BusinessException(ErrorCode.TRIP_PLAN_NOT_FOUND);
        }
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
    }

    @Transactional
    public void deletePlaceById(Long tripPlanId, Long placeId) {
        TripPlan tripPlan = tripPlanRepository.findById(tripPlanId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRIP_PLAN_NOT_FOUND));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));

        if (!place.getTripPlan().getId().equals(tripPlan.getId())) {
            throw new BusinessException(ErrorCode.INVALID_PLACE_FOR_TRIP_PLAN);
        }

        placeRepository.deleteById(placeId);
    }
}
