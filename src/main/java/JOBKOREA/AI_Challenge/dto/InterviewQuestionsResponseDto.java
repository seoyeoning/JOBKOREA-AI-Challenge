package JOBKOREA.AI_Challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionsResponseDto {
    
    private List<QuestionDto> questions;
    private String analysis;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        private String question;
        private String category;
        private String difficulty;
        private String expectedAnswer;
        private String tips;
    }
}
