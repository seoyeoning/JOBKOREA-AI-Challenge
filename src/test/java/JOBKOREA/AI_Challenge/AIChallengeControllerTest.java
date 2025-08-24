package JOBKOREA.AI_Challenge;

import JOBKOREA.AI_Challenge.controller.AIChallengeController;
import JOBKOREA.AI_Challenge.dto.InterviewQuestionsResponseDto;
import JOBKOREA.AI_Challenge.dto.LearningPathResponseDto;
import JOBKOREA.AI_Challenge.dto.ResumeRequestDto;
import JOBKOREA.AI_Challenge.service.OpenAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;



@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("integration")
@DisplayName("AI Challenge API 통합 테스트")
class AIChallengeControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private OpenAIService openAIService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @LocalServerPort
    private int port;
    
    private ResumeRequestDto sampleResumeRequest;
    
    @BeforeEach
    void setUp() {
        sampleResumeRequest = ResumeRequestDto.builder()
                .careerSummary("3년차 백엔드 개발자")
                .jobDescription("Spring Boot/MSA/Python 기반 커머스 서비스 개발")
                .technicalSkills("Java, Spring Boot, MSA, Python, AWS EC2")
                .additionalInfo("AWS EC2 운영 경험 보유")
                .build();
    }
    
    @Nested
    @DisplayName("면접 질문 생성 API 테스트")
    @Tag("interview-questions")
    class InterviewQuestionsTests {
        
        @Test
        @DisplayName("유효한 이력서 정보로 면접 질문 생성 시 성공")
        void generateInterviewQuestions_ValidRequest_ReturnsSuccess() throws Exception {
        // Given
        String url = "http://localhost:" + port + "/api/v1/ai-challenge/interview-questions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        
        HttpEntity<ResumeRequestDto> request = new HttpEntity<>(sampleResumeRequest, headers);
        
        // When
        ResponseEntity<InterviewQuestionsResponseDto> response = restTemplate.exchange(
                url, HttpMethod.POST, request, InterviewQuestionsResponseDto.class);
        
        // Then
        org.junit.jupiter.api.Assertions.assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "응답 상태가 성공이어야 합니다. 실제: " + response.getStatusCode());
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody(), 
            "응답 본문이 null이 아니어야 합니다");
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody().getQuestions(), 
            "질문 목록이 null이 아니어야 합니다");
        org.junit.jupiter.api.Assertions.assertTrue(response.getBody().getQuestions().size() > 0, 
            "질문이 최소 1개 이상 있어야 합니다. 실제: " + response.getBody().getQuestions().size());
        
        System.out.println("=== 실제 OpenAI API 응답 (면접 질문) ===");
        System.out.println("응답 상태: " + response.getStatusCode());
        System.out.println("생성된 질문 수: " + response.getBody().getQuestions().size());
        System.out.println("첫 번째 질문: " + response.getBody().getQuestions().get(0).getQuestion());
        System.out.println("분석: " + response.getBody().getAnalysis());
        System.out.println("=====================================");
        }
    }
    
    @Nested
    @DisplayName("학습 경로 추천 API 테스트")
    @Tag("learning-path")
    class LearningPathTests {
        
        @Test
        @DisplayName("유효한 이력서 정보로 학습 경로 추천 시 성공")
        void generateLearningPath_ValidRequest_ReturnsSuccess() throws Exception {
        // Given
        String url = "http://localhost:" + port + "/api/v1/ai-challenge/learning-path";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        
        HttpEntity<ResumeRequestDto> request = new HttpEntity<>(sampleResumeRequest, headers);
        
        // When
        ResponseEntity<LearningPathResponseDto> response = restTemplate.exchange(
                url, HttpMethod.POST, request, LearningPathResponseDto.class);
        
        // Then
        org.junit.jupiter.api.Assertions.assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "응답 상태가 성공이어야 합니다. 실제: " + response.getStatusCode());
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody(), 
            "응답 본문이 null이 아니어야 합니다");
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody().getLearningSteps(), 
            "학습 단계가 null이 아니어야 합니다");
        org.junit.jupiter.api.Assertions.assertTrue(response.getBody().getLearningSteps().size() > 0, 
            "학습 단계가 최소 1개 이상 있어야 합니다. 실제: " + response.getBody().getLearningSteps().size());
        
        System.out.println("=== 실제 OpenAI API 응답 (학습 경로) ===");
        System.out.println("응답 상태: " + response.getStatusCode());
        System.out.println("전체 요약: " + response.getBody().getSummary());
        System.out.println("학습 단계 수: " + response.getBody().getLearningSteps().size());
        System.out.println("첫 번째 단계: " + response.getBody().getLearningSteps().get(0).getStep());
        System.out.println("예상 소요 시간: " + response.getBody().getEstimatedDuration());
        System.out.println("=====================================");
        }
    }
    
    @Nested
    @DisplayName("유효성 검사 테스트")
    @Tag("validation")
    class ValidationTests {
        
        @Test
        @DisplayName("잘못된 이력서 정보로 면접 질문 생성 시 유효성 검사 실패")
        void generateInterviewQuestions_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given
        ResumeRequestDto invalidRequest = ResumeRequestDto.builder()
                .careerSummary("") // 빈 값으로 유효성 검사 실패
                .jobDescription("Spring Boot 개발")
                .technicalSkills("Java, Spring")
                .build();
        
        String url = "http://localhost:" + port + "/api/v1/ai-challenge/interview-questions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        
        HttpEntity<ResumeRequestDto> request = new HttpEntity<>(invalidRequest, headers);
        
        // When & Then
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);
        
        org.junit.jupiter.api.Assertions.assertTrue(response.getStatusCode().is4xxClientError(), 
            "잘못된 요청에 대해 4xx 클라이언트 에러가 반환되어야 합니다. 실제: " + response.getStatusCode());
        
        System.out.println("=== 유효성 검사 실패 테스트 ===");
        System.out.println("응답 상태: " + response.getStatusCode());
        System.out.println("응답 내용: " + response.getBody());
        System.out.println("=============================");
        }
    }
    
    @Nested
    @DisplayName("기본 API 테스트")
    @Tag("health")
    class BasicAPITests {
        
        @Test
        @DisplayName("헬스 체크 API 정상 동작 확인")
        void health_ReturnsSuccess() throws Exception {
        // When & Then
        String url = "http://localhost:" + port + "/api/v1/ai-challenge/health";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        org.junit.jupiter.api.Assertions.assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "헬스 체크 응답이 성공이어야 합니다. 실제: " + response.getStatusCode());
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody(), 
            "헬스 체크 응답 본문이 null이 아니어야 합니다");
        org.junit.jupiter.api.Assertions.assertTrue(response.getBody().contains("AI Challenge API is running!"), 
            "응답에 'AI Challenge API is running!'이 포함되어야 합니다. 실제: " + response.getBody());
        
        System.out.println("=== 헬스 체크 테스트 ===");
        System.out.println("응답 상태: " + response.getStatusCode());
        System.out.println("응답 내용: " + response.getBody());
        System.out.println("=====================");
        }
    }
}
