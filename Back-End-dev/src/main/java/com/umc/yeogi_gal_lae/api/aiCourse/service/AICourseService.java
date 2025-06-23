package com.umc.yeogi_gal_lae.api.aiCourse.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.yeogi_gal_lae.api.aiCourse.domain.AICourse;
import com.umc.yeogi_gal_lae.api.aiCourse.repository.AICourseRepository;
import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.repository.PlaceRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;

@Service
public class AICourseService {
    private static final Logger logger = LoggerFactory.getLogger(AICourseService.class);

    private final AICourseRepository aiCourseRepository;
    private final PlaceRepository placeRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @Value("${openai.api.key}")
    private String apiKey;

    public AICourseService(AICourseRepository aiCourseRepository,
                           PlaceRepository placeRepository,
                           WebClient.Builder webClientBuilder,
                           ObjectMapper objectMapper,
                           NotificationService notificationService) {
        this.aiCourseRepository = aiCourseRepository;
        this.placeRepository = placeRepository;
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    /**
     * GPT API를 호출하여 TripPlan에 속한 Place들로 일정(각 일차별 추천 Place 목록)을 생성하고, 새 AICourse 엔티티로 저장한 후 저장된 엔티티를 반환합니다.
     *
     * @param tripPlan 여행 계획 엔티티
     * @return 생성된 AICourse 엔티티 (생성 실패 시 null 반환)
     */
    @Transactional
    public AICourse generateAndStoreAICourse(TripPlan tripPlan) {
        // 1. "코스 짜기 시작" 알림
        notificationService.createStartNotification(
                tripPlan.getRoom().getName(), // 방 이름
                tripPlan.getUser().getUsername(), // 사용자 이름
                tripPlan.getUser().getEmail(), // 사용자 이메일
                NotificationType.COURSE_START, // 알림 타입
                tripPlan.getId(),
                tripPlan.getTripPlanType()
        );
        // TripPlan에 직접 연결된 Place들을 DB에서 명시적으로 조회
        List<Place> places = placeRepository.findAllByTripPlanId(tripPlan.getId());

        if (places.isEmpty()) {
            logger.warn("TripPlan id {}에 등록된 장소가 없습니다.", tripPlan.getId());
        } else {
            List<String> placeNames = places.stream()
                    .map(Place::getPlaceName)
                    .collect(Collectors.toList());
            logger.info("TripPlan id {}에 등록된 장소 목록: {}", tripPlan.getId(), placeNames);
        }
        if (places.isEmpty()) {
            return null;
        }
        // 프롬프트 구성
        String prompt = buildPrompt(tripPlan, places);
        // GPT API 호출
        String gptApiResponse = callGptApi(prompt);
        // GPT 응답 파싱 (예: {"1일차": ["Place A", "Place B"], ...})
        Map<String, List<String>> courseByDay = parseGptResponse(gptApiResponse);
        // GPT 결과를 실제 Place 객체와 매핑
        Map<String, List<Place>> course = new LinkedHashMap<>();
        Set<Long> usedPlaceIds = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : courseByDay.entrySet()) {
            String dayLabel = entry.getKey();
            // 해당 일차의 추천 장소들을 실제 Place 객체로 매핑
            List<Place> recommendedPlaces = entry.getValue().stream()
                    .map(name -> findPlaceByName(places, name))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 이미 사용된 장소를 제외하여 유니크한 장소만 할당 (글로벌 중복 제거)
            List<Place> uniqueForDay = recommendedPlaces.stream()
                    .filter(place -> !usedPlaceIds.contains(place.getId()))
                    .collect(Collectors.toList());

            // 선택된 장소들을 전역 사용 목록에 추가
            uniqueForDay.forEach(place -> usedPlaceIds.add(place.getId()));

            course.put(dayLabel, uniqueForDay);
        }
        // 저장할 데이터를 위해 각 일차별 Place 이름 목록으로 변환
        Map<String, List<String>> courseByName = new LinkedHashMap<>();
        for (Map.Entry<String, List<Place>> entry : course.entrySet()) {
            List<String> placeNames = entry.getValue().stream()
                    .map(Place::getPlaceName)
                    .collect(Collectors.toList());
            courseByName.put(entry.getKey(), placeNames);
        }
        try {
            String courseJson = objectMapper.writeValueAsString(courseByName);
            // 새로운 AICourse 엔티티 생성 (업데이트 없이 새로 insert)
            AICourse aiCourse = AICourse.builder()
                    .tripPlan(tripPlan)
                    .courseJson(courseJson)
                    .build();
            // "코스 짜기 완료" 알림 추가
            notificationService.createEndNotification(
                    tripPlan.getRoom().getName(), // 방 이름
                    tripPlan.getUser().getEmail(), // 사용자 이메일
                    NotificationType.COURSE_COMPLETE, // 알림 타입
                    tripPlan.getId(),
                    tripPlan.getTripPlanType()
            );
            return aiCourseRepository.save(aiCourse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 저장된 AICourse 엔티티에서 일정 데이터를 읽어와 도메인 모델로 변환하여 반환합니다.
     *
     * @param id AICourse 엔티티의 기본키
     * @return Map: key는 "1일차", value는 해당 일차의 Place 리스트
     */
    public Map<String, List<Place>> getStoredAICourseById(Long id) {
        Optional<AICourse> aiCourseOpt = aiCourseRepository.findById(id);
        if (aiCourseOpt.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            String courseJson = aiCourseOpt.get().getCourseJson();
            Map<String, List<String>> courseByDay =
                    objectMapper.readValue(courseJson, new TypeReference<Map<String, List<String>>>() {
                    });
            // TripPlan에 속한 Place들을 직접 사용
            List<Place> places = aiCourseOpt.get().getTripPlan().getPlaces();
            Map<String, List<Place>> course = new LinkedHashMap<>();
            for (Map.Entry<String, List<String>> entry : courseByDay.entrySet()) {
                List<Place> dayPlaces = entry.getValue().stream()
                        .map(name -> findPlaceByName(places, name))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                course.put(entry.getKey(), dayPlaces);
            }
            return course;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // --- 프롬프트 구성, GPT API 호출, 응답 파싱, Place 매칭 메서드 ---
    private String buildPrompt(TripPlan tripPlan, List<Place> places) {
        long totalDays = ChronoUnit.DAYS.between(tripPlan.getStartDate(), tripPlan.getEndDate()) + 1;
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("다음 여행 정보를 기반으로 효율적인 일정을 생성해줘.\n")
                .append("여행 시작일: ").append(tripPlan.getStartDate()).append("\n")
                .append("여행 종료일: ").append(tripPlan.getEndDate()).append("\n")
                .append("총 여행 일수: ").append(totalDays).append("일\n")
                .append("여행 지역: ").append(tripPlan.getLocation()).append("\n\n")
                .append("다음은 해당 여행 계획에 등록된 방문 가능한 장소 목록입니다.\n")
                .append("※ 일정 생성 시 반드시 아래 목록에 있는 장소 이름만 사용하며, 동일한 장소가 중복되지 않도록 해 주세요.\n");
        for (Place p : places) {
            promptBuilder.append("- ").append(p.getPlaceName())
                    .append(" (주소: ").append(p.getAddress())
                    .append(", 좌표: (").append(p.getLatitude()).append(", ")
                    .append(p.getLongitude()).append("))\n");
        }
        promptBuilder.append("\n")
                .append("위 정보를 바탕으로, 총 ").append(totalDays)
                .append("일의 여행 일정(각 일차에 방문할 장소 추천)을 생성해줘.\n")
                .append("일정은 반드시 '1일차', '2일차', ... '").append(totalDays)
                .append("일차' 형식의 키를 가지며, 각 키의 값은 위 목록에 있는 장소들의 이름만 포함해야 합니다.\n")
                .append("예시:\n")
                .append("{\"1일차\": [\"장소 A\", \"장소 B\"], \"2일차\": [\"장소 C\", \"장소 D\"], ...}");
        return promptBuilder.toString();
    }


    private String callGptApi(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        requestBody.put("messages", List.of(message));
        String response = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("GPT API 응답: {}", response);
        return response;
    }

    private Map<String, List<String>> parseGptResponse(String gptResponse) {
        try {
            JsonNode root = objectMapper.readTree(gptResponse);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            int jsonStart = content.indexOf('{');
            if (jsonStart != -1) {
                content = content.substring(jsonStart);
            }
            return objectMapper.readValue(content, new TypeReference<Map<String, List<String>>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private Place findPlaceByName(List<Place> places, String name) {
        String normalizedName = name.trim().toLowerCase();
        return places.stream()
                .filter(p -> p.getPlaceName().trim().toLowerCase().equals(normalizedName))
                .findFirst()
                .orElse(null);
    }
}
