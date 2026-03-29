package org.ganjp.api.cms.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private String id;
    private String name;
    private String originalUrl;
    private String sourceName;
    private String filename;
    private String fileUrl;
    private String thumbnailFilename;
    private String thumbnailUrl;
    private String extension;
    private String mimeType;
    private Long sizeBytes;
    private Integer width;
    private Integer height;
    private String altText;
    private String tags;
    private Image.Language lang;
    private Integer displayOrder;
    private String createdBy;
    private String updatedBy;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ImageResponse from(Image image, String baseUrl) {
        String fileUrl = null;
        if (image.getFilename() != null) {
            fileUrl = baseUrl + "/v1/images/view/" + image.getFilename();
        }
        String thumbUrl = null;
        if (image.getThumbnailFilename() != null) {
            thumbUrl = baseUrl + "/v1/images/view/" + image.getThumbnailFilename();
        }
        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .originalUrl(image.getOriginalUrl())
                .sourceName(image.getSourceName())
                .filename(image.getFilename())
                .fileUrl(fileUrl)
                .thumbnailFilename(image.getThumbnailFilename())
                .thumbnailUrl(thumbUrl)
                .extension(image.getExtension())
                .mimeType(image.getMimeType())
                .sizeBytes(image.getSizeBytes())
                .width(image.getWidth())
                .height(image.getHeight())
                .altText(image.getAltText())
                .tags(image.getTags())
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
