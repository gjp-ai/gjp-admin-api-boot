package org.ganjp.api.edu.fillblankquestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FillBlankQuestionUpdateRequest {

    private String question;
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
    private FillBlankQuestion.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
