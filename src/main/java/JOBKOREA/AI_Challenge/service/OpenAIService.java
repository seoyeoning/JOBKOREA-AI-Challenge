package JOBKOREA.AI_Challenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import JOBKOREA.AI_Challenge.dto.InterviewQuestionsResponseDto;
import JOBKOREA.AI_Challenge.dto.LearningPathResponseDto;
import JOBKOREA.AI_Challenge.dto.ResumeRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {
    
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;
    
    @Value("${openai.api.model}")
    private String model;
    
    @Value("${openai.api.max-tokens}")
    private Integer maxTokens;
    
    @Value("${openai.api.temperature}")
    private Double temperature;
    
    public InterviewQuestionsResponseDto generateInterviewQuestions(ResumeRequestDto resumeRequest) throws java.net.SocketTimeoutException {
        try {
            String prompt = createInterviewQuestionsPrompt(resumeRequest);
            String response = callOpenAI(prompt);
            return parseInterviewQuestionsResponse(response, resumeRequest);
        } catch (Exception e) {
            log.error("면접 질문 생성 중 오류 발생", e);
            throw new RuntimeException("면접 질문 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }
    
    public LearningPathResponseDto generateLearningPath(ResumeRequestDto resumeRequest) throws java.net.SocketTimeoutException {
        try {
            String prompt = createLearningPathPrompt(resumeRequest);
            String response = callOpenAI(prompt);
            return parseLearningPathResponse(response, resumeRequest);
        } catch (Exception e) {
            log.error("학습 경로 생성 중 오류 발생", e);
            throw new RuntimeException("학습 경로 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }
    
    private String createInterviewQuestionsPrompt(ResumeRequestDto resumeRequest) {
        return String.format("""
            이력서 기반 면접 질문 5개 생성 (응답은 3000자 이하):
            경력: %s
            직무: %s
            기술: %s
            추가: %s
            
            요구사항:
            - 기술적 역량, 프로젝트 경험, 문제 해결, 팀워크, 성장 동기 카테고리
            - 난이도: 초급/중급/고급
            - 각 질문에 예상 답변과 팁 포함
            - 응답은 3000자 이하로 간결하게 작성
            - 핵심 내용을 우선으로 하고, 불필요한 설명은 생략
            
            완전한 JSON 응답 필수:
            {
              "questions": [
                {
                  "question": "질문",
                  "category": "카테고리",
                  "difficulty": "난이도",
                  "expectedAnswer": "예상 답변",
                  "tips": "팁"
                }
              ],
              "analysis": "분석"
            }
            
            중요: JSON 형식이 완전해야 하며, 응답이 중간에 끊기지 않도록 주의해주세요.
            """, 
            resumeRequest.getCareerSummary(),
            resumeRequest.getJobDescription(),
            resumeRequest.getTechnicalSkills(),
            resumeRequest.getAdditionalInfo() != null ? resumeRequest.getAdditionalInfo() : "없음"
        );
    }
    
    private String createLearningPathPrompt(ResumeRequestDto resumeRequest) {
        return String.format("""
            이력서 기반 맞춤형 학습 경로 제안 (응답은 3000자 이하):
            경력: %s
            직무: %s
            기술: %s
            추가: %s
            
            요구사항:
            - 현재 역량 분석 및 개선점 파악
            - 기술 스택 심화, 프로젝트 경험, 커뮤니케이션 스킬 강화 방안
            - 우선순위와 예상 소요 시간 포함
            - 실현 가능한 현실적 가이드
            - 응답은 3000자 이하로 간결하게 작성
            - 구체적이고 실용적인 내용 위주로 작성
            
            완전한 JSON 응답 필수:
            {
              "summary": "전체 요약",
              "learningSteps": [
                {
                  "step": "단계명",
                  "description": "상세 설명",
                  "priority": "우선순위",
                  "resources": "학습 자료 및 방법"
                }
              ],
              "estimatedDuration": "전체 예상 소요 시간"
            }
            
            중요: JSON 형식이 완전해야 하며, 응답이 중간에 끊기지 않도록 주의해주세요.
            """,
            resumeRequest.getCareerSummary(),
            resumeRequest.getJobDescription(),
            resumeRequest.getTechnicalSkills(),
            resumeRequest.getAdditionalInfo() != null ? resumeRequest.getAdditionalInfo() : "없음"
        );
    }
    
    private String callOpenAI(String prompt) throws java.net.SocketTimeoutException {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", prompt));
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();
        
        try {
            String response = openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
            log.debug("OpenAI API 응답: {}", response);
            return response;
        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생: {}", e.getMessage(), e);
            
            // SocketTimeoutException을 RuntimeException으로 래핑하여 Controller에서 처리하도록 함
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                throw new RuntimeException("OpenAI API 타임아웃: " + e.getMessage(), e.getCause());
            }
            
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage(), e);
        }
    }
    
    private InterviewQuestionsResponseDto parseInterviewQuestionsResponse(String response, ResumeRequestDto resumeRequest) throws JsonProcessingException {
        try {
            log.debug("파싱할 OpenAI 응답: {}", response);
            
            // 응답이 null이거나 비어있는지 확인
            if (response == null || response.trim().isEmpty()) {
                log.warn("OpenAI 응답이 비어있음");
                throw new RuntimeException("AI 서비스 응답이 비어있습니다. 잠시 후 다시 시도해주세요.");
            }
            
            // JSON 응답이 완전한지 확인 (더 정확한 검증)
            String trimmedResponse = response.trim();
            if (!trimmedResponse.startsWith("{") || !trimmedResponse.endsWith("}")) {
                log.warn("OpenAI 응답이 유효한 JSON 형식이 아님: {}", response);
                throw new RuntimeException("AI 서비스 응답 형식에 문제가 있습니다. 잠시 후 다시 시도해주세요.");
            }
            
            // 중괄호 균형 확인
            int openBraces = 0, closeBraces = 0;
            for (char c : trimmedResponse.toCharArray()) {
                if (c == '{') openBraces++;
                if (c == '}') closeBraces++;
            }
            
            if (openBraces != closeBraces) {
                log.warn("OpenAI 응답의 중괄호가 균형잡히지 않음 (열린: {}, 닫힌: {}): {}", openBraces, closeBraces, response);
                throw new RuntimeException("AI 서비스 응답이 완전하지 않습니다. 잠시 후 다시 시도해주세요.");
            }
            
            // 응답 길이 확인 (3000자 이하)
            if (trimmedResponse.length() > 3000) {
                log.warn("OpenAI 응답이 너무 김 ({}자): {}", trimmedResponse.length(), response);
                throw new RuntimeException("AI 서비스 응답이 너무 깁니다. 잠시 후 다시 시도해주세요.");
            }
            
            JsonNode jsonNode = objectMapper.readTree(response);
            List<InterviewQuestionsResponseDto.QuestionDto> questions = new ArrayList<>();
            
            JsonNode questionsNode = jsonNode.get("questions");
            if (questionsNode != null && questionsNode.isArray()) {
                for (JsonNode questionNode : questionsNode) {
                    try {
                        InterviewQuestionsResponseDto.QuestionDto question = InterviewQuestionsResponseDto.QuestionDto.builder()
                                .question(getSafeText(questionNode, "question"))
                                .category(getSafeText(questionNode, "category"))
                                .difficulty(getSafeText(questionNode, "difficulty"))
                                .expectedAnswer(getSafeText(questionNode, "expectedAnswer"))
                                .tips(getSafeText(questionNode, "tips"))
                                .build();
                        questions.add(question);
                    } catch (Exception e) {
                        log.warn("질문 파싱 중 오류 발생, 건너뜀: {}", e.getMessage());
                        continue;
                    }
                }
            }
            
            // 최소한 하나의 질문이 있어야 함
            if (questions.isEmpty()) {
                log.warn("파싱된 질문이 없음");
                throw new RuntimeException("AI 서비스에서 질문을 생성하지 못했습니다. 잠시 후 다시 시도해주세요.");
            }
            
            String analysis = jsonNode.has("analysis") ? jsonNode.get("analysis").asText() : "";
            
            return InterviewQuestionsResponseDto.builder()
                    .questions(questions)
                    .analysis(analysis)
                    .build();
                    
        } catch (Exception e) {
            log.error("면접 질문 응답 파싱 중 오류 발생. 응답: {}", response, e);
            throw new RuntimeException("AI 서비스 응답 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }
    
    private LearningPathResponseDto parseLearningPathResponse(String response, ResumeRequestDto resumeRequest) throws JsonProcessingException {
        try {
            log.debug("파싱할 OpenAI 응답: {}", response);
            
            // 응답이 null이거나 비어있는지 확인
            if (response == null || response.trim().isEmpty()) {
                log.warn("OpenAI 응답이 비어있음");
                throw new RuntimeException("AI 서비스 응답이 비어있습니다. 잠시 후 다시 시도해주세요.");
            }
            
            // JSON 응답이 완전한지 확인 (더 정확한 검증)
            String trimmedResponse = response.trim();
            if (!trimmedResponse.startsWith("{") || !trimmedResponse.endsWith("}")) {
                log.warn("OpenAI 응답이 유효한 JSON 형식이 아님: {}", response);
                throw new RuntimeException("AI 서비스 응답 형식에 문제가 있습니다. 잠시 후 다시 시도해주세요.");
            }
            
            // 중괄호 균형 확인
            int openBraces = 0, closeBraces = 0;
            for (char c : trimmedResponse.toCharArray()) {
                if (c == '{') openBraces++;
                if (c == '}') closeBraces++;
            }
            
            if (openBraces != closeBraces) {
                log.warn("OpenAI 응답의 중괄호가 균형잡히지 않음 (열린: {}, 닫힌: {}): {}", openBraces, closeBraces, response);
                throw new RuntimeException("AI 서비스 응답이 완전하지 않습니다. 잠시 후 다시 시도해주세요.");
            }
            
            // 응답 길이 확인 (3000자 이하)
            if (trimmedResponse.length() > 3000) {
                log.warn("OpenAI 응답이 너무 깁니다 ({}자): {}", trimmedResponse.length(), response);
                throw new RuntimeException("AI 서비스 응답이 너무 깁니다. 잠시 후 다시 시도해주세요.");
            }
            
            JsonNode jsonNode = objectMapper.readTree(response);
            
            String summary = jsonNode.has("summary") ? jsonNode.get("summary").asText() : "";
            String estimatedDuration = jsonNode.has("estimatedDuration") ? jsonNode.get("estimatedDuration").asText() : "";
            
            List<LearningPathResponseDto.LearningStepDto> learningSteps = new ArrayList<>();
            JsonNode stepsNode = jsonNode.get("learningSteps");
            if (stepsNode != null && stepsNode.isArray()) {
                for (JsonNode stepNode : stepsNode) {
                    try {
                        LearningPathResponseDto.LearningStepDto step = LearningPathResponseDto.LearningStepDto.builder()
                                .step(getSafeText(stepNode, "step"))
                                .description(getSafeText(stepNode, "description"))
                                .priority(getSafeText(stepNode, "priority"))
                                .resources(getSafeText(stepNode, "resources"))
                                .build();
                        learningSteps.add(step);
                    } catch (Exception e) {
                        log.warn("학습 단계 파싱 중 오류 발생, 건너뜀: {}", e.getMessage());
                        continue;
                    }
                }
            }
            
            // 최소한 하나의 학습 단계가 있어야 함
            if (learningSteps.isEmpty()) {
                log.warn("파싱된 학습 단계가 없음");
                throw new RuntimeException("AI 서비스에서 학습 단계를 생성하지 못했습니다. 잠시 후 다시 시도해주세요.");
            }
            
            return LearningPathResponseDto.builder()
                    .learningSteps(learningSteps)
                    .summary(summary)
                    .estimatedDuration(estimatedDuration)
                    .build();
                    
        } catch (Exception e) {
            log.error("학습 경로 응답 파싱 중 오류 발생. 응답: {}", response, e);
            throw new RuntimeException("AI 서비스 응답 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }
    

    
    /**
     * JsonNode에서 안전하게 텍스트를 추출하는 헬퍼 메서드
     */
    private String getSafeText(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull()) {
            return fieldNode.asText();
        }
        return "정보 없음";
    }
}
