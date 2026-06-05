package org.ganjp.api.edu.multiplechoicequestion;

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
public class MultipleChoiceQuestionCreateRequest {

    @NotBlank(message = "Question is required")
    private String question;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    @NotBlank(message = "Answer is required")
    private String answer;

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
    private MultipleChoiceQuestion.Language lang;

    private Integer displayOrder;
    private Boolean isActive;
}
