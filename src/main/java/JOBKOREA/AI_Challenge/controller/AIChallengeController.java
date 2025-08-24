package JOBKOREA.AI_Challenge.controller;

import JOBKOREA.AI_Challenge.dto.InterviewQuestionsResponseDto;
import JOBKOREA.AI_Challenge.dto.LearningPathResponseDto;
import JOBKOREA.AI_Challenge.dto.ResumeRequestDto;
import JOBKOREA.AI_Challenge.service.OpenAIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai-challenge")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AIChallengeController {
    
    private final OpenAIService openAIService;
    
    /**
     * 맞춤형 면접 질문 생성 API
     * 이력서 정보를 바탕으로 실제 면접에서 나올 법한 심층적인 질문 5개를 생성합니다.
     */
    @PostMapping("/interview-questions")
    public ResponseEntity<InterviewQuestionsResponseDto> generateInterviewQuestions(
            @Valid @RequestBody ResumeRequestDto resumeRequest) {
        
        log.info("면접 질문 생성 요청: {}", resumeRequest);
        
        try {
            InterviewQuestionsResponseDto response = openAIService.generateInterviewQuestions(resumeRequest);
            log.info("면접 질문 생성 완료: {}개 질문 생성", response.getQuestions().size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("면접 질문 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 맞춤형 학습 경로 추천 API
     * 이력서 정보를 분석하여 합격률을 높일 수 있는 개인 맞춤형 학습 경로를 제안합니다.
     */
    @PostMapping("/learning-path")
    public ResponseEntity<LearningPathResponseDto> generateLearningPath(
            @Valid @RequestBody ResumeRequestDto resumeRequest) {
        
        log.info("학습 경로 추천 요청: {}", resumeRequest);
        
        try {
            LearningPathResponseDto response = openAIService.generateLearningPath(resumeRequest);
            log.info("학습 경로 추천 완료: {}개 단계 제안", response.getLearningSteps().size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("학습 경로 추천 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 통합 API - 면접 질문과 학습 경로를 한 번에 생성
     */
    @PostMapping("/comprehensive")
    public ResponseEntity<ComprehensiveResponse> generateComprehensive(
            @Valid @RequestBody ResumeRequestDto resumeRequest) {
        
        log.info("통합 분석 요청: {}", resumeRequest);
        
        try {
            InterviewQuestionsResponseDto interviewQuestions = openAIService.generateInterviewQuestions(resumeRequest);
            LearningPathResponseDto learningPath = openAIService.generateLearningPath(resumeRequest);
            
            ComprehensiveResponse response = ComprehensiveResponse.builder()
                    .interviewQuestions(interviewQuestions)
                    .learningPath(learningPath)
                    .build();
            
            log.info("통합 분석 완료");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("통합 분석 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 헬스 체크 API
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Challenge API is running!");
    }
    
    /**
     * 통합 응답을 위한 내부 클래스
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ComprehensiveResponse {
        private InterviewQuestionsResponseDto interviewQuestions;
        private LearningPathResponseDto learningPath;
    }
}
