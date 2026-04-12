package org.ganjp.api.cms.website;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for website response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteResponse {

    private String id;
    private String name;
    private String url;
    private String logoUrl;
    private String description;
    private String tags;
    private Website.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Convert from entity to response DTO
     */
    public static WebsiteResponse from(Website website, String baseUrl) {
        String logoUrl = website.getLogoUrl();
        if (logoUrl != null && !logoUrl.isBlank() && !logoUrl.startsWith("http")) {
            // Construct full path for locally stored logos: base + /v1/logos/view/ + filename
            String prefix = baseUrl;
            if (prefix != null && !prefix.isBlank()) {
                if (!prefix.endsWith("/")) prefix += "/";
                logoUrl = prefix + "v1/logos/view/" + logoUrl;
            }
        }
        
        return WebsiteResponse.builder()
                .id(website.getId())
                .name(website.getName())
                .url(website.getUrl())
                .logoUrl(logoUrl)
                .description(website.getDescription())
                .tags(website.getTags())
                .lang(website.getLang())
                .displayOrder(website.getDisplayOrder())
                .isActive(website.getIsActive())
                .createdAt(website.getCreatedAt())
                .updatedAt(website.getUpdatedAt())
                .createdBy(website.getCreatedBy())
                .updatedBy(website.getUpdatedBy())
                .build();
    }

    /**
     * Get tags as array
     */
    public String[] getTagsArray() {
        if (tags == null || tags.trim().isEmpty()) {
            return new String[0];
        }
        return tags.split(",");
    }
}
