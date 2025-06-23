package com.umc.yeogi_gal_lae.api.budget.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.yeogi_gal_lae.api.aiCourse.domain.AICourse;
import com.umc.yeogi_gal_lae.api.aiCourse.repository.AICourseRepository;
import com.umc.yeogi_gal_lae.api.budget.domain.Budget;
import com.umc.yeogi_gal_lae.api.budget.dto.BudgetAssignment;
import com.umc.yeogi_gal_lae.api.budget.repository.BudgetRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final AICourseRepository aiCourseRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;


    @Value("${openai.api.key}")
    private String apiKey;

    public BudgetService(BudgetRepository budgetRepository,
                         AICourseRepository aiCourseRepository,
                         WebClient.Builder webClientBuilder,
                         ObjectMapper objectMapper,
                         NotificationService notificationService) {
        this.budgetRepository = budgetRepository;
        this.aiCourseRepository = aiCourseRepository;
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }
    /**
     * 저장된 AICourse의 스케줄 데이터를 기반으로 GPT API를 호출하여, 각 일차별 각 장소에 대해 예산 추천(예: budgetType 및 추천 금액)을 산출하고, 그 결과를 Budget 엔티티에
     * 저장합니다.
     *
     * @param aiCourseId AICourse 엔티티의 기본키
     * @return 생성된 Budget 엔티티, 실패 시 null 반환
     */
    @Transactional
    public Budget generateAndStoreBudget(Long aiCourseId) {
        Optional<AICourse> aiCourseOpt = aiCourseRepository.findById(aiCourseId);
        if (!aiCourseOpt.isPresent()) {
            return null;
        }
        AICourse aiCourse = aiCourseOpt.get();
        // "예산 정하기 시작" 알림 추가
        notificationService.createStartNotification(
                aiCourse.getTripPlan().getRoom().getName(), // 방 이름
                aiCourse.getTripPlan().getUser().getUsername(), // 사용자 이름
                aiCourse.getTripPlan().getUser().getEmail(), // 사용자 이메일
                NotificationType.BUDGET_START, // 알림 타입 (추가 필요)
                aiCourse.getTripPlan().getId(),
                aiCourse.getTripPlan().getTripPlanType()
        );
        // aiCourse에 저장된 courseJson은 이미 일정(일차별 장소 이름 목록)을 포함하고 있음
        String scheduleJson = aiCourse.getCourseJson();
        // 프롬프트 구성: GPT에게 스케줄 정보를 바탕으로 각 장소의 예산 추천을 요청
        String prompt = buildBudgetPrompt(scheduleJson);
        // GPT API 호출
        String gptApiResponse = callGptApi(prompt);
        // 응답 파싱: 예시 결과 JSON은 Map<String, List<BudgetAssignment>>
        Map<String, List<BudgetAssignment>> budgetMap = parseBudgetGptResponse(gptApiResponse);
        // "예산 정하기 완료" 알림 추가
        notificationService.createEndNotification(
                aiCourse.getTripPlan().getRoom().getName(), // 방 이름
                aiCourse.getTripPlan().getUser().getEmail(), // 사용자 이메일
                NotificationType.BUDGET_COMPLETE, // 알림 타입 (추가 필요)
                aiCourse.getTripPlan().getId(),
                aiCourse.getTripPlan().getTripPlanType()
        );
        try {
            String budgetJson = objectMapper.writeValueAsString(budgetMap);
            Budget budget = Budget.builder()
                    .aiCourse(aiCourse)
                    .budgetJson(budgetJson)
                    .build();
            return budgetRepository.save(budget);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildBudgetPrompt(String scheduleJson) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Given the following travel schedule in JSON format: ")
                .append(scheduleJson)
                .append(", generate budget recommendations for each day. ");
        prompt.append("단, 'placeName' 필드의 값은 반드시 한국어로만 작성되어야 하며, 영어 표현은 사용하지 마세요. ");
        prompt.append(
                "For each place, assign exactly one budget type (one of MEAL, ACTIVITY, SHOPPING, TRANSPORT) and a recommended amount. ");
        prompt.append(
                "Output the result as a JSON object where each key is the day (e.g., '1일차') and each value is an array of objects with the fields: 'placeName', 'budgetType', and 'recommendedAmount'. ");
        prompt.append("Example output:\n");
        prompt.append("{\n");
        prompt.append("  \"1일차\": [\n");
        prompt.append(
                "    {\"placeName\": \"Example Place\", \"budgetType\": \"ACTIVITY\", \"recommendedAmount\": 20000},\n");
        prompt.append(
                "    {\"placeName\": \"Example Restaurant\", \"budgetType\": \"MEAL\", \"recommendedAmount\": 15000}\n");
        prompt.append("  ],\n");
        prompt.append("  \"2일차\": [\n");
        prompt.append(
                "    {\"placeName\": \"Another Place\", \"budgetType\": \"ACTIVITY\", \"recommendedAmount\": 25000}\n");
        prompt.append("  ]\n");
        prompt.append("}");
        return prompt.toString();
    }

    private String callGptApi(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        requestBody.put("messages", List.of(message));
        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private Map<String, List<BudgetAssignment>> parseBudgetGptResponse(String gptResponse) {
        try {
            JsonNode root = objectMapper.readTree(gptResponse);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            int jsonStart = content.indexOf('{');
            if (jsonStart != -1) {
                content = content.substring(jsonStart);
            }
            return objectMapper.readValue(content, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    public Map<String, List<BudgetAssignment>> getBudgetMapById(Long id) {
        Optional<Budget> budgetOpt = budgetRepository.findById(id);
        if (budgetOpt.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            String budgetJson = budgetOpt.get().getBudgetJson();
            return objectMapper.readValue(budgetJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
