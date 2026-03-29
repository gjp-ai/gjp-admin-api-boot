package org.ganjp.api.cms.article.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ganjp.api.cms.article.image.ArticleImage.Language;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleImageResponse {
    private String id;
    private String articleId;
    private String articleTitle;
    private String filename;
    private String fileUrl;
    private String originalUrl;
    private Integer width;
    private Integer height;
    private Language lang;
    private Integer displayOrder;
    private String createdBy;
    private String updatedBy;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ArticleImageResponse from(ArticleImage image, String baseUrl) {
        String fileUrl = null;
        if (image.getFilename() != null) {
            fileUrl = baseUrl + "/v1/article-images/view/" + image.getFilename();
        }
        return ArticleImageResponse.builder()
                .id(image.getId())
                .articleId(image.getArticleId())
                .articleTitle(image.getArticleTitle())
                .filename(image.getFilename())
                .fileUrl(fileUrl)
                .originalUrl(image.getOriginalUrl())
                .width(image.getWidth())
                .height(image.getHeight())
                .lang(image.getLang())
                .displayOrder(image.getDisplayOrder())
                .createdBy(image.getCreatedBy())
                .updatedBy(image.getUpdatedBy())
                .isActive(image.getIsActive())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }
}
