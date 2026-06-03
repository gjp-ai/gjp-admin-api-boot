package org.ganjp.api.edu.sentence;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "edu.sentence.upload")
public class SentenceUploadProperties {
    private String audioDirectory;
}
