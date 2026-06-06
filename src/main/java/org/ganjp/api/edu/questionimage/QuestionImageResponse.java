package org.ganjp.api.edu.questionimage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionImageResponse {
    private String id;
    private String multipleChoiceQuestionId;
    private String freeTextQuestionId;
    private String trueFalseQuestionId;
    private String fillBlankQuestionId;
    private String filename;
    private String fileUrl;
    private String originalUrl;
    private Integer width;
    private Integer height;
    private QuestionImage.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static QuestionImageResponse from(QuestionImage image, String baseUrl) {
        if (image == null) {
            return null;
        }
        String fileUrl = image.getFilename() != null && baseUrl != null
                ? baseUrl + "/v1/edu-question-images/view/" + image.getFilename()
                : null;
        return QuestionImageResponse.builder()
                .id(image.getId())
                .multipleChoiceQuestionId(image.getMultipleChoiceQuestionId())
                .freeTextQuestionId(image.getFreeTextQuestionId())
                .trueFalseQuestionId(image.getTrueFalseQuestionId())
                .fillBlankQuestionId(image.getFillBlankQuestionId())
                .filename(image.getFilename())
                .fileUrl(fileUrl)
                .originalUrl(image.getOriginalUrl())
                .width(image.getWidth())
                .height(image.getHeight())
                .lang(image.getLang())
                .displayOrder(image.getDisplayOrder())
                .isActive(image.getIsActive())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .createdBy(image.getCreatedBy())
                .updatedBy(image.getUpdatedBy())
                .build();
    }
}
