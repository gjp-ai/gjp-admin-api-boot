package org.ganjp.api.cms.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {
    private String id;
    private String title;
    private String summary;
    private String content;
    private String originalUrl;
    private String sourceName;
    private String coverImageFilename;
    private String coverImageUrl;
    private String coverImageOriginalUrl;
    private String tags;
    private Article.Language lang;
    private Integer displayOrder;
    private String createdBy;
    private String updatedBy;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ArticleResponse from(Article article, String baseUrl) {
        String coverUrl = null;
        if (article.getCoverImageFilename() != null) {
            coverUrl = baseUrl + "/v1/articles/cover/" + article.getCoverImageFilename();
        }
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .summary(article.getSummary())
                .content(article.getContent())
                .originalUrl(article.getOriginalUrl())
                .sourceName(article.getSourceName())
                .coverImageFilename(article.getCoverImageFilename())
                .coverImageUrl(coverUrl)
                .coverImageOriginalUrl(article.getCoverImageOriginalUrl())
                .tags(article.getTags())
                .lang(article.getLang())
                .displayOrder(article.getDisplayOrder())
                .createdBy(article.getCreatedBy())
                .updatedBy(article.getUpdatedBy())
                .isActive(article.getIsActive())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}
