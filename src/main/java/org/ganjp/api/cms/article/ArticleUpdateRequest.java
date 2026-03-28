package org.ganjp.api.cms.article;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleUpdateRequest {
    @Size(max = 255)
    private String title;

    @Size(max = 500)
    private String summary;

    private String content;

    @Size(max = 500)
    private String originalUrl;

    @Size(max = 255)
    private String sourceName;

    @Size(max = 500)
    private String coverImageFilename;

    @Size(max = 500)
    private String coverImageOriginalUrl;

    private MultipartFile coverImageFile;

    @Size(max = 500)
    private String tags;

    private Article.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
