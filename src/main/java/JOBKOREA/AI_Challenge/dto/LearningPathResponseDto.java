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
public class LearningPathResponseDto {
    
    private List<LearningStepDto> learningSteps;
    private String summary;
    private String estimatedDuration;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningStepDto {
        private String step;
        private String description;
        private String priority;
        private String resources;
    }
}
