package org.ganjp.api.edu.vocabulary;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "edu.vocabulary.upload")
public class VocabularyUploadProperties {
    private String audioDirectory;
}
