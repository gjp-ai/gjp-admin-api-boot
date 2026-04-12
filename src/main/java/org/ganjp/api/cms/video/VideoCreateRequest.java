package org.ganjp.api.cms.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoCreateRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 255, message = "Filename must not exceed 255 characters")
    private String filename;

    private MultipartFile file;

    @Size(max = 500, message = "Original URL must not exceed 500 characters")
    private String originalUrl;

    @Size(max = 255, message = "Source name must not exceed 255 characters")
    private String sourceName;

    private String coverImageFilename;
    private MultipartFile coverImageFile;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    private Video.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
