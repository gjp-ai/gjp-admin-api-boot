package org.ganjp.api.edu.vocabulary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyResponse {

    private String id;
    private String name;
    private String phoneticUs;
    private String phoneticUsAudioFilename;
    private String phoneticUsAudioUrl;
    private String phoneticUsAudioOriginalUrl;
    private String phoneticUk;
    private String phoneticUkAudioFilename;
    private String phoneticUkAudioUrl;
    private String phoneticUkAudioOriginalUrl;
    private String partOfSpeech;
    private String synonyms;
    private String translation;
    private String meaningClue;
    private String meaning;
    private String easyMeaning;
    private String sentenceOne;
    private String sentenceTwo;
    private String difficultyLevel;
    private String dictionaryUrl;
    private String additionalInfo;
    private Integer term;
    private Integer week;
    private String channel;
    private String tags;
    private Vocabulary.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static VocabularyResponse from(Vocabulary vocabulary, String baseUrl) {
        if (vocabulary == null) {
            return null;
        }
        String usAudioUrl = vocabulary.getPhoneticUsAudioFilename() != null && baseUrl != null
                ? baseUrl + "/v1/edu-vocabularies/audios/" + vocabulary.getPhoneticUsAudioFilename()
                : null;
        String ukAudioUrl = vocabulary.getPhoneticUkAudioFilename() != null && baseUrl != null
                ? baseUrl + "/v1/edu-vocabularies/audios/" + vocabulary.getPhoneticUkAudioFilename()
                : null;
        return VocabularyResponse.builder()
                .id(vocabulary.getId())
                .name(vocabulary.getName())
                .phoneticUs(vocabulary.getPhoneticUs())
                .phoneticUsAudioFilename(vocabulary.getPhoneticUsAudioFilename())
                .phoneticUsAudioUrl(usAudioUrl)
                .phoneticUsAudioOriginalUrl(vocabulary.getPhoneticUsAudioOriginalUrl())
                .phoneticUk(vocabulary.getPhoneticUk())
                .phoneticUkAudioFilename(vocabulary.getPhoneticUkAudioFilename())
                .phoneticUkAudioUrl(ukAudioUrl)
                .phoneticUkAudioOriginalUrl(vocabulary.getPhoneticUkAudioOriginalUrl())
                .partOfSpeech(vocabulary.getPartOfSpeech())
                .synonyms(vocabulary.getSynonyms())
                .translation(vocabulary.getTranslation())
                .meaningClue(vocabulary.getMeaningClue())
                .meaning(vocabulary.getMeaning())
                .easyMeaning(vocabulary.getEasyMeaning())
                .sentenceOne(vocabulary.getSentenceOne())
                .sentenceTwo(vocabulary.getSentenceTwo())
                .difficultyLevel(vocabulary.getDifficultyLevel())
                .dictionaryUrl(vocabulary.getDictionaryUrl())
                .additionalInfo(vocabulary.getAdditionalInfo())
                .term(vocabulary.getTerm())
                .week(vocabulary.getWeek())
                .channel(vocabulary.getChannel())
                .tags(vocabulary.getTags())
                .lang(vocabulary.getLang())
                .displayOrder(vocabulary.getDisplayOrder())
                .isActive(vocabulary.getIsActive())
                .createdAt(vocabulary.getCreatedAt())
                .updatedAt(vocabulary.getUpdatedAt())
                .createdBy(vocabulary.getCreatedBy())
                .updatedBy(vocabulary.getUpdatedBy())
                .build();
    }
}
