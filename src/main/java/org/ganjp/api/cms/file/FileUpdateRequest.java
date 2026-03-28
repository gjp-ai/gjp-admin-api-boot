package org.ganjp.api.cms.file;

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
public class FileUpdateRequest {
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String originalUrl;

    @Size(max = 255)
    private String sourceName;

    private MultipartFile file;

    @Size(max = 255)
    private String filename; // optional desired filename

    @Size(max = 500)
    private String tags;

    private FileAsset.Language lang;
    private Integer displayOrder;
    private Boolean isActive;
}
