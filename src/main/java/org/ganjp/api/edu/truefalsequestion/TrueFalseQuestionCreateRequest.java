package org.ganjp.api.edu.truefalsequestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrueFalseQuestionCreateRequest {

    @NotBlank(message = "Question is required")
    private String question;

    @NotNull(message = "Answer is required")
    private TrueFalseQuestion.Answer answer;

    private String explanation;
    private String difficultyLevel;
    private String gradeLevel;
    private String subject;
    private String topic;
    private Integer term;
    private Integer week;
    private String channel;
    private String tags;

    @NotNull(message = "Language is required")
    private TrueFalseQuestion.Language lang;

    private Integer displayOrder;
    private Boolean isActive;
}
