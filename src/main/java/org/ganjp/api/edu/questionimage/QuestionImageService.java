package org.ganjp.api.edu.questionimage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.common.config.CmsProperties;
import org.ganjp.api.common.exception.BusinessException;
import org.ganjp.api.common.exception.ResourceNotFoundException;
import org.ganjp.api.common.util.CmsUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionImageService {

    private final QuestionImageRepository questionImageRepository;
    private final QuestionImageUploadProperties questionImageUploadProperties;
    private final CmsProperties cmsProperties;

    public QuestionImageResponse createQuestionImage(QuestionImageCreateRequest request, String userId) {
        if (!request.hasQuestionReference()) {
            throw new BusinessException("At least one question ID is required");
        }
        if (!request.hasImageSource()) {
            throw new BusinessException("File or original URL is required");
        }

        try {
            String targetFilename = request.getFilename().trim();
            String targetExtension = getFileExtension(targetFilename);
            BufferedImage bufferedImage;
            String sourceExtension;

            MultipartFile file = request.getFile();
            if (file != null && !file.isEmpty()) {
                sourceExtension = getFileExtension(file.getOriginalFilename());
                bufferedImage = ImageIO.read(file.getInputStream());
            } else {
                URL url = new URL(request.getOriginalUrl());
                sourceExtension = getFileExtension(url.getPath());
                bufferedImage = ImageIO.read(url);
            }

            if (bufferedImage == null) {
                throw new BusinessException("Failed to read image data");
            }

            String finalExtension = targetExtension;
            if (finalExtension == null || finalExtension.isBlank()) {
                finalExtension = sourceExtension == null || sourceExtension.isBlank() ? "png" : sourceExtension;
                targetFilename = targetFilename + "." + finalExtension;
            }
            if (targetFilename.length() > 60) {
                throw new BusinessException("Filename must not exceed 60 characters");
            }
            if (questionImageRepository.existsByFilename(targetFilename)) {
                throw new BusinessException("Question image filename already exists: " + targetFilename);
            }

            Files.createDirectories(Path.of(questionImageUploadProperties.getDirectory()));
            Path filePath = CmsUtil.resolveSecurePath(questionImageUploadProperties.getDirectory(), targetFilename);
            if (Files.exists(filePath)) {
                throw new BusinessException("File already exists: " + targetFilename);
            }
            if (!ImageIO.write(bufferedImage, finalExtension, filePath.toFile())) {
                throw new BusinessException("Unsupported image extension: " + finalExtension);
            }

            QuestionImage image = QuestionImage.builder()
                    .id(UUID.randomUUID().toString())
                    .multipleChoiceQuestionId(request.getMultipleChoiceQuestionId())
                    .freeTextQuestionId(request.getFreeTextQuestionId())
                    .trueFalseQuestionId(request.getTrueFalseQuestionId())
                    .fillBlankQuestionId(request.getFillBlankQuestionId())
                    .filename(targetFilename)
                    .originalUrl(request.getOriginalUrl())
                    .width(bufferedImage.getWidth())
                    .height(bufferedImage.getHeight())
                    .lang(request.getLang() != null ? request.getLang() : QuestionImage.Language.EN)
                    .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                    .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                    .build();
            image.setCreatedBy(userId);
            image.setUpdatedBy(userId);

            return toResponse(questionImageRepository.save(image));
        } catch (IOException e) {
            log.error("Failed to create question image", e);
            throw new UncheckedIOException("Failed to store question image", e);
        }
    }

    public QuestionImageResponse updateQuestionImage(String id, QuestionImageUpdateRequest request, String userId) {
        QuestionImage image = questionImageRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionImage", "id", id));

        if (request.getMultipleChoiceQuestionId() != null) image.setMultipleChoiceQuestionId(request.getMultipleChoiceQuestionId());
        if (request.getFreeTextQuestionId() != null) image.setFreeTextQuestionId(request.getFreeTextQuestionId());
        if (request.getTrueFalseQuestionId() != null) image.setTrueFalseQuestionId(request.getTrueFalseQuestionId());
        if (request.getFillBlankQuestionId() != null) image.setFillBlankQuestionId(request.getFillBlankQuestionId());
        if (request.getOriginalUrl() != null) image.setOriginalUrl(request.getOriginalUrl());
        if (request.getLang() != null) image.setLang(request.getLang());
        if (request.getDisplayOrder() != null) image.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) image.setIsActive(request.getIsActive());
        image.setUpdatedBy(userId);

        return toResponse(questionImageRepository.save(image));
    }

    public void deleteQuestionImage(String id, String userId) {
        QuestionImage image = questionImageRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionImage", "id", id));
        image.setIsActive(false);
        image.setUpdatedBy(userId);
        questionImageRepository.save(image);
    }

    public void permanentlyDeleteQuestionImage(String id) {
        QuestionImage image = questionImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionImage", "id", id));
        if (image.getFilename() != null) {
            CmsUtil.moveToDeletedFolder(CmsUtil.resolveSecurePath(questionImageUploadProperties.getDirectory(), image.getFilename()));
        }
        questionImageRepository.delete(image);
    }

    @Transactional(readOnly = true)
    public QuestionImageResponse getQuestionImageById(String id) {
        return questionImageRepository.findByIdAndIsActiveTrue(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionImage", "id", id));
    }

    @Transactional(readOnly = true)
    public List<QuestionImageResponse> listByMultipleChoiceQuestion(String multipleChoiceQuestionId) {
        return questionImageRepository.findByMultipleChoiceQuestionIdAndIsActiveTrueOrderByDisplayOrderAsc(multipleChoiceQuestionId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionImageResponse> listByFreeTextQuestion(String freeTextQuestionId) {
        return questionImageRepository.findByFreeTextQuestionIdAndIsActiveTrueOrderByDisplayOrderAsc(freeTextQuestionId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionImageResponse> listByTrueFalseQuestion(String trueFalseQuestionId) {
        return questionImageRepository.findByTrueFalseQuestionIdAndIsActiveTrueOrderByDisplayOrderAsc(trueFalseQuestionId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionImageResponse> listByFillBlankQuestion(String fillBlankQuestionId) {
        return questionImageRepository.findByFillBlankQuestionIdAndIsActiveTrueOrderByDisplayOrderAsc(fillBlankQuestionId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionImageResponse> searchQuestionImages(String multipleChoiceQuestionId, String freeTextQuestionId,
                                                           String trueFalseQuestionId, String fillBlankQuestionId,
                                                           QuestionImage.Language lang, Boolean isActive) {
        return questionImageRepository.searchQuestionImages(multipleChoiceQuestionId, freeTextQuestionId,
                        trueFalseQuestionId, fillBlankQuestionId, lang, isActive)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public java.io.File getImageFile(String filename) {
        return CmsUtil.resolveSecurePath(questionImageUploadProperties.getDirectory(), filename).toFile();
    }

    private QuestionImageResponse toResponse(QuestionImage image) {
        return QuestionImageResponse.from(image, cmsProperties.getBaseUrl());
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) {
            return null;
        }
        return filename.substring(lastDot + 1).toLowerCase();
    }
}
