package org.ganjp.api.cms.article;

import jakarta.validation.constraints.NotBlank;
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
public class ArticleCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 500, message = "Summary must not exceed 500 characters")
    private String summary;

    private String content;

    @Size(max = 500, message = "Original URL must not exceed 500 characters")
    private String originalUrl;

    @Size(max = 255, message = "Source name must not exceed 255 characters")
    private String sourceName;

    private String coverImageFilename;

    @Size(max = 500, message = "Cover image URL must not exceed 500 characters")
    private String coverImageOriginalUrl;

    private MultipartFile coverImageFile;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    private Article.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
