package org.ganjp.api.edu.sentence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentenceResponse {

    private String id;
    private String name;
    private String phonetic;
    private String phoneticAudioFilename;
    private String phoneticAudioUrl;
    private String translation;
    private String explanation;
    private String difficultyLevel;
    private String channel;
    private String tags;
    private Sentence.Language lang;
    private Integer displayOrder;
    private Integer term;
    private Integer week;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static SentenceResponse from(Sentence sentence, String baseUrl) {
        if (sentence == null) {
            return null;
        }
        String audioUrl = sentence.getPhoneticAudioFilename() != null && baseUrl != null
                ? baseUrl + "/v1/edu-sentences/audios/" + sentence.getPhoneticAudioFilename()
                : null;
        return SentenceResponse.builder()
                .id(sentence.getId())
                .name(sentence.getName())
                .phonetic(sentence.getPhonetic())
                .phoneticAudioFilename(sentence.getPhoneticAudioFilename())
                .phoneticAudioUrl(audioUrl)
                .translation(sentence.getTranslation())
                .explanation(sentence.getExplanation())
                .difficultyLevel(sentence.getDifficultyLevel())
                .channel(sentence.getChannel())
                .tags(sentence.getTags())
                .lang(sentence.getLang())
                .displayOrder(sentence.getDisplayOrder())
                .term(sentence.getTerm())
                .week(sentence.getWeek())
                .isActive(sentence.getIsActive())
                .createdAt(sentence.getCreatedAt())
                .updatedAt(sentence.getUpdatedAt())
                .createdBy(sentence.getCreatedBy())
                .updatedBy(sentence.getUpdatedBy())
                .build();
    }
}
