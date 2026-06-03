package org.ganjp.api.edu.sentence;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentenceCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String phonetic;
    private String phoneticAudioFilename;
    private MultipartFile phoneticAudioFile;
    private String translation;
    private String explanation;
    private String difficultyLevel;
    private String channel;
    private String tags;

    @NotNull(message = "Language is required")
    private Sentence.Language lang;

    private Integer displayOrder;
    private Integer term;
    private Integer week;
    private Boolean isActive;
}
