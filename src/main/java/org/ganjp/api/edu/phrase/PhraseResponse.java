package org.ganjp.api.edu.phrase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhraseResponse {

    private String id;
    private String name;
    private String phonetic;
    private String phoneticAudioFilename;
    private String phoneticAudioUrl;
    private String phoneticAudioOriginalUrl;
    private String synonyms;
    private String translation;
    private String meaningClue;
    private String meaning;
    private String easyMeaning;
    private String sentenceOne;
    private String sentenceTwo;
    private String difficultyLevel;
    private String dictionaryUrl;
    private Integer term;
    private Integer week;
    private String channel;
    private String tags;
    private Phrase.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static PhraseResponse from(Phrase phrase, String baseUrl) {
        if (phrase == null) {
            return null;
        }
        String audioUrl = phrase.getPhoneticAudioFilename() != null && baseUrl != null
                ? baseUrl + "/v1/edu-phrases/audios/" + phrase.getPhoneticAudioFilename()
                : null;
        return PhraseResponse.builder()
                .id(phrase.getId())
                .name(phrase.getName())
                .phonetic(phrase.getPhonetic())
                .phoneticAudioFilename(phrase.getPhoneticAudioFilename())
                .phoneticAudioUrl(audioUrl)
                .phoneticAudioOriginalUrl(phrase.getPhoneticAudioOriginalUrl())
                .synonyms(phrase.getSynonyms())
                .translation(phrase.getTranslation())
                .meaningClue(phrase.getMeaningClue())
                .meaning(phrase.getMeaning())
                .easyMeaning(phrase.getEasyMeaning())
                .sentenceOne(phrase.getSentenceOne())
                .sentenceTwo(phrase.getSentenceTwo())
                .difficultyLevel(phrase.getDifficultyLevel())
                .dictionaryUrl(phrase.getDictionaryUrl())
                .term(phrase.getTerm())
                .week(phrase.getWeek())
                .channel(phrase.getChannel())
                .tags(phrase.getTags())
                .lang(phrase.getLang())
                .displayOrder(phrase.getDisplayOrder())
                .isActive(phrase.getIsActive())
                .createdAt(phrase.getCreatedAt())
                .updatedAt(phrase.getUpdatedAt())
                .createdBy(phrase.getCreatedBy())
                .updatedBy(phrase.getUpdatedBy())
                .build();
    }
}
