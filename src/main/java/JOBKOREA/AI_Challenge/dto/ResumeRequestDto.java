package JOBKOREA.AI_Challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeRequestDto {
    
    @NotBlank(message = "경력 요약은 필수입니다")
    @Size(max = 1000, message = "경력 요약은 1000자 이하여야 합니다")
    private String careerSummary;
    
    @NotBlank(message = "수행 직무는 필수입니다")
    @Size(max = 1000, message = "수행 직무는 1000자 이하여야 합니다")
    private String jobDescription;
    
    @NotBlank(message = "기술 스킬은 필수입니다")
    @Size(max = 2000, message = "기술 스킬은 2000자 이하여야 합니다")
    private String technicalSkills;
    
    @Size(max = 500, message = "추가 정보는 500자 이하여야 합니다")
    private String additionalInfo;
}
