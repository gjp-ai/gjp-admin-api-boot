package org.ganjp.api.edu.sentence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentenceUpdateRequest {

    private String name;
    private String phonetic;
    private String phoneticAudioFilename;
    private MultipartFile phoneticAudioFile;
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
}
