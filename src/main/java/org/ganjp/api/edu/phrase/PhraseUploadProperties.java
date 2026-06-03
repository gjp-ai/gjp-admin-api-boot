package org.ganjp.api.edu.phrase;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "edu.phrase.upload")
public class PhraseUploadProperties {
    private String audioDirectory;
}
