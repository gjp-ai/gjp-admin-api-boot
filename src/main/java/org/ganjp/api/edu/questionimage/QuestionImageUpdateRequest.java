package org.ganjp.api.edu.questionimage;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionImageUpdateRequest {

    @Size(max = 36)
    private String multipleChoiceQuestionId;

    @Size(max = 36)
    private String freeTextQuestionId;

    @Size(max = 36)
    private String trueFalseQuestionId;

    @Size(max = 36)
    private String fillBlankQuestionId;

    @Size(max = 256)
    private String originalUrl;

    private QuestionImage.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
