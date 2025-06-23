package com.umc.yeogi_gal_lae.api.place.controller;

import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.request.PlaceRequest;
import com.umc.yeogi_gal_lae.api.place.dto.response.AllPlaceResponse;
import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import com.umc.yeogi_gal_lae.api.place.service.PlaceService;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trip-plans")
public class PlaceController {

    private final PlaceService placeService;

    @Operation(summary = "여행 장소 추가 (여러 개)")
    @PostMapping("/{tripPlanId}/places")
    public Response<List<PlaceResponse>> addPlaces(@PathVariable Long tripPlanId,
                                                   @RequestBody List<PlaceRequest> requests) {
        List<Place> places = placeService.addPlaces(tripPlanId, requests);
        List<PlaceResponse> responses = places.stream()
                .map(PlaceConverter::toPlaceResponse)
                .collect(Collectors.toList());
        return Response.of(SuccessCode.OK, responses);
    }

    @Operation(summary = "특정 장소 가져오기")
    @GetMapping("/{tripPlanId}/places/{placeId}")
    public Response<PlaceResponse> getPlaceById(
            @PathVariable Long tripPlanId,
            @PathVariable Long placeId) {
        Place place = placeService.getPlaceById(tripPlanId, placeId);
        PlaceResponse response = PlaceConverter.toPlaceResponse(place);
        return Response.of(SuccessCode.PLACES_FETCHED, response);
    }

    @Operation(summary = "특정 장소 삭제")
    @DeleteMapping("/{tripPlanId}/places/{placeId}")
    public Response<Void> deletePlace(
            @PathVariable Long tripPlanId,
            @PathVariable Long placeId) {
        placeService.deletePlaceById(tripPlanId, placeId);
        return Response.ok(SuccessCode.PLACE_DELETED);
    }

    @Operation(summary = "모든 장소 가져오기")
    @GetMapping("/{tripPlanId}/places")
    public Response<AllPlaceResponse> getAllPlaces(@PathVariable Long tripPlanId) {
        List<Place> places = placeService.getAllPlacesByTripPlanId(tripPlanId);
        AllPlaceResponse response = PlaceConverter.toAllPlaceResponse(places);
        return Response.of(SuccessCode.OK, response);
    }
}
