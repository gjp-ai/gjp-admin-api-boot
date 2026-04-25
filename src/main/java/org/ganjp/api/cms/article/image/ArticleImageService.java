package org.ganjp.api.cms.article.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.ganjp.api.cms.article.ArticleProperties;
import org.ganjp.api.common.config.CmsProperties;
import org.ganjp.api.common.exception.ResourceNotFoundException;
import org.ganjp.api.common.util.CmsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleImageService {
    private final ArticleImageRepository articleImageRepository;
    private final CmsProperties cmsProperties;
    private final ArticleProperties articleProperties;

    @Transactional(readOnly = true)
    public org.springframework.core.io.Resource getImage(String filename) {
        try {
            Path filePath = CmsUtil.resolveSecurePath(articleProperties.getContentImage().getUpload().getDirectory(), filename);
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalStateException("Could not read the file: " + filename);
            }
        } catch (java.net.MalformedURLException e) {
            throw new IllegalStateException("Error reading file: " + filename, e);
        }
    }

    @Transactional(readOnly = true)
    public java.io.File getImageFile(String filename) {
        Path filePath = CmsUtil.resolveSecurePath(articleProperties.getContentImage().getUpload().getDirectory(), filename);
        return filePath.toFile();
    }

    @Transactional(readOnly = true)
    public ArticleImageResponse getArticleImageById(String id) {
        ArticleImage image = articleImageRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("ArticleImage", "id", id));
        return toResponse(image);
    }

    @Transactional(readOnly = true)
    public List<ArticleImageResponse> listArticleImages(String articleId) {
        List<ArticleImage> images = articleImageRepository.findByArticleIdAndIsActiveTrueOrderByDisplayOrderAsc(articleId);
        return images.stream().map(this::toResponse).toList();
    }

    public ArticleImageResponse createArticleImage(ArticleImageCreateRequest request, String userId) {
        try {
            if (request.getFilename() == null || request.getFilename().trim().isEmpty()) {
                throw new IllegalArgumentException("Filename is required");
            }

            String targetFilename = request.getFilename();
            String targetExtension = CmsUtil.getFileExtension(targetFilename);
            
            BufferedImage bufferedImage;
            String sourceExtension;

            if (request.getFile() != null && !request.getFile().isEmpty()) {
                MultipartFile file = request.getFile();
                String originalFilename = file.getOriginalFilename();
                sourceExtension = CmsUtil.getFileExtension(originalFilename);
                bufferedImage = ImageIO.read(file.getInputStream());
            } else if (request.getOriginalUrl() != null && !request.getOriginalUrl().trim().isEmpty()) {
                String originalUrl = request.getOriginalUrl();
                try (java.io.InputStream inputStream = CmsUtil.getInputStreamFromUrl(originalUrl)) {
                    bufferedImage = ImageIO.read(inputStream);
                }
                sourceExtension = CmsUtil.getFileExtension(originalUrl.split("\\?")[0]);
            } else {
                 throw new IllegalArgumentException("File or Original URL is required");
            }

            if (bufferedImage == null) {
                 throw new IllegalArgumentException("Failed to read image data");
            }

            String finalExtension;
            if (targetExtension != null && !targetExtension.isEmpty()) {
                finalExtension = targetExtension;
            } else {
                if (sourceExtension != null && !sourceExtension.isEmpty()) {
                    finalExtension = sourceExtension;
                } else {
                    finalExtension = "png";
                }
                targetFilename = targetFilename + "." + finalExtension;
            }

            // WebP → PNG/JPG conversion
            if (CmsUtil.isWebpExtension(finalExtension) && bufferedImage != null) {
                finalExtension = CmsUtil.resolveWebpOutputFormat(bufferedImage);
                bufferedImage = CmsUtil.prepareForOutput(bufferedImage, finalExtension);
                targetFilename = CmsUtil.replaceExtension(targetFilename, finalExtension);
                log.info("Converted WebP article image to {}: {}", finalExtension.toUpperCase(), targetFilename);
            }

            Path uploadPath = Paths.get(articleProperties.getContentImage().getUpload().getDirectory());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = CmsUtil.resolveSecurePath(articleProperties.getContentImage().getUpload().getDirectory(), targetFilename);
            if (Files.exists(filePath)) {
                throw new IllegalArgumentException("File with name " + targetFilename + " already exists");
            }
            ImageIO.write(bufferedImage, finalExtension, filePath.toFile());
            
            Integer width = bufferedImage.getWidth();
            Integer height = bufferedImage.getHeight();

            ArticleImage articleImage = ArticleImage.builder()
                    .id(UUID.randomUUID().toString())
                    .articleId(request.getArticleId())
                    .articleTitle(request.getArticleTitle())
                    .filename(targetFilename)
                    .originalUrl(request.getOriginalUrl())
                    .width(width)
                    .height(height)
                    .lang(request.getLang())
                    .displayOrder(request.getDisplayOrder())
                    .isActive(request.getIsActive())
                    .build();
            articleImage.setCreatedBy(userId);
            articleImage.setUpdatedBy(userId);

            ArticleImage saved = articleImageRepository.save(articleImage);
            return toResponse(saved);
        } catch (IOException e) {
            log.error("Error creating article image", e);
            throw new java.io.UncheckedIOException("Failed to store file", e);
        }
    }

    public ArticleImageResponse updateArticleImage(String id, ArticleImageUpdateRequest request, String userId) {
        ArticleImage image = articleImageRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("ArticleImage", "id", id));

        if (request.getArticleId() != null) image.setArticleId(request.getArticleId());
        if (request.getArticleTitle() != null) image.setArticleTitle(request.getArticleTitle());
        if (request.getOriginalUrl() != null) image.setOriginalUrl(request.getOriginalUrl());
        if (request.getLang() != null) image.setLang(request.getLang());
        if (request.getDisplayOrder() != null) image.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) image.setIsActive(request.getIsActive());

        image.setUpdatedBy(userId);

        ArticleImage saved = articleImageRepository.save(image);
        return toResponse(saved);
    }

    public void deleteArticleImage(String id) {
        Optional<ArticleImage> imageOpt = articleImageRepository.findById(id);
        if (imageOpt.isPresent()) {
            ArticleImage image = imageOpt.get();
            // Soft delete
            image.setIsActive(false);
            articleImageRepository.save(image);
        }
    }

    public void deleteArticleImagePermanently(String id) {
        Optional<ArticleImage> imageOpt = articleImageRepository.findById(id);
        if (imageOpt.isPresent()) {
            ArticleImage image = imageOpt.get();
            
            // Move file to deleted folder
            if (image.getFilename() != null && !image.getFilename().isBlank()) {
                CmsUtil.moveToDeletedFolder(CmsUtil.resolveSecurePath(articleProperties.getContentImage().getUpload().getDirectory(), image.getFilename()));
            }
            
            articleImageRepository.delete(image);
        }
    }
    
    @Transactional(readOnly = true)
    public List<ArticleImageResponse> searchArticleImages(String articleId, ArticleImage.Language lang, Boolean isActive) {
        return articleImageRepository.searchArticleImages(articleId, lang, isActive)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ArticleImageResponse> searchArticleImages(String articleId, ArticleImage.Language lang, Boolean isActive, Pageable pageable) {
        return articleImageRepository.searchArticleImages(articleId, lang, isActive, pageable)
                .map(this::toResponse);
    }

    private int[] getImageDimensions(Path filePath) {
        try {
            BufferedImage img = ImageIO.read(filePath.toFile());
            if (img != null) {
                return new int[]{img.getWidth(), img.getHeight()};
            }
        } catch (Exception e) {
            log.warn("Could not read image dimensions for {}", filePath);
        }
        return new int[0];
    }

    private ArticleImageResponse toResponse(ArticleImage image) {
        return ArticleImageResponse.from(image, cmsProperties.getBaseUrl());
    }
}
