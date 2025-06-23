package com.umc.yeogi_gal_lae.api.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlaceResponse {

    private Long id;
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String image;
    private String description;
    private String userName;
    private String profileImage;

}
