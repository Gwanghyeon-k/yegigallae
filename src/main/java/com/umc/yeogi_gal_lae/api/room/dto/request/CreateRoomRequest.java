
package com.umc.yeogi_gal_lae.api.room.dto.request;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class CreateRoomRequest {
    @NotBlank(message = "방 이름은 필수 입력 값입니다.")  // 빈 값 또는 공백 불가
    @Size(max = 50, message = "방 이름은 50자 이하여야 합니다.")  // 최대 길이 제한
    private String roomName; // 방 이름

    @NotBlank(message = "최소 한 명 이상의 사용자를 추가해야 합니다.")
    private List<Long> userIds;

}