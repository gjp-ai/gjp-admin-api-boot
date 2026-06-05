package org.ganjp.api.edu.truefalsequestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrueFalseQuestionUpdateRequest {

    private String question;
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
    private TrueFalseQuestion.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
