package org.ganjp.api.edu.questionimage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionImageCreateRequest {

    @Size(max = 36)
    private String multipleChoiceQuestionId;

    @Size(max = 36)
    private String freeTextQuestionId;

    @Size(max = 36)
    private String trueFalseQuestionId;

    @Size(max = 36)
    private String fillBlankQuestionId;

    @NotBlank(message = "Filename is required")
    @Size(max = 60, message = "Filename must not exceed 60 characters")
    private String filename;

    @Pattern(regexp = "^https?://.*", message = "Original URL must be a valid HTTP/HTTPS URL")
    @Size(max = 256, message = "Original URL must not exceed 256 characters")
    private String originalUrl;

    private MultipartFile file;

    @Builder.Default
    private QuestionImage.Language lang = QuestionImage.Language.EN;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean isActive = true;

    public boolean hasImageSource() {
        return (file != null && !file.isEmpty()) || (originalUrl != null && !originalUrl.trim().isEmpty());
    }

    public boolean hasQuestionReference() {
        return (multipleChoiceQuestionId != null && !multipleChoiceQuestionId.trim().isEmpty()) ||
                (freeTextQuestionId != null && !freeTextQuestionId.trim().isEmpty()) ||
                (trueFalseQuestionId != null && !trueFalseQuestionId.trim().isEmpty()) ||
                (fillBlankQuestionId != null && !fillBlankQuestionId.trim().isEmpty());
    }
}
