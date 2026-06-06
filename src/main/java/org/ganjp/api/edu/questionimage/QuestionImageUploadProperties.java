package org.ganjp.api.edu.questionimage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "edu.question-image.upload")
public class QuestionImageUploadProperties {
    private String directory;
}
