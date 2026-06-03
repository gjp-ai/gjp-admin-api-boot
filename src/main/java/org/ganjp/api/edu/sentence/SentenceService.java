package org.ganjp.api.edu.sentence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.common.config.CmsProperties;
import org.ganjp.api.common.exception.BusinessException;
import org.ganjp.api.common.exception.ResourceNotFoundException;
import org.ganjp.api.common.util.CmsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SentenceService {

    private final SentenceRepository sentenceRepository;
    private final SentenceUploadProperties sentenceUploadProperties;
    private final CmsProperties cmsProperties;

    public SentenceResponse createSentence(SentenceCreateRequest request, String createdBy) {
        String channel = StringUtils.hasText(request.getChannel()) ? request.getChannel() : "All";
        Sentence.Language lang = request.getLang() != null ? request.getLang() : Sentence.Language.EN;

        if (sentenceRepository.existsByNameAndChannelAndLang(request.getName(), channel, lang)) {
            throw new BusinessException(String.format("Sentence '%s' already exists for channel '%s' and language '%s'",
                    request.getName(), channel, lang));
        }

        Sentence sentence = Sentence.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .phonetic(request.getPhonetic())
                .translation(request.getTranslation())
                .explanation(request.getExplanation())
                .difficultyLevel(request.getDifficultyLevel())
                .channel(channel)
                .tags(request.getTags())
                .lang(lang)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .term(request.getTerm())
                .week(request.getWeek())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        handleAudioUpload(
                sentence,
                request.getPhoneticAudioFile(),
                request.getPhoneticAudioFilename(),
                sentence::setPhoneticAudioFilename,
                "phonetic"
        );
        sentence.setCreatedBy(createdBy);
        sentence.setUpdatedBy(createdBy);

        return toResponse(sentenceRepository.save(sentence));
    }

    public SentenceResponse updateSentence(String id, SentenceUpdateRequest request, String updatedBy) {
        Sentence sentence = sentenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sentence", "id", id));

        String name = request.getName() != null ? request.getName() : sentence.getName();
        String channel = request.getChannel() != null ? request.getChannel() : sentence.getChannel();
        Sentence.Language lang = request.getLang() != null ? request.getLang() : sentence.getLang();

        if ((request.getName() != null || request.getChannel() != null || request.getLang() != null)
                && sentenceRepository.existsByNameAndChannelAndLangExcludingId(name, channel, lang, id)) {
            throw new BusinessException(String.format("Sentence '%s' already exists for channel '%s' and language '%s'",
                    name, channel, lang));
        }

        if (request.getName() != null) sentence.setName(request.getName());
        if (request.getPhonetic() != null) sentence.setPhonetic(request.getPhonetic());
        if (request.getTranslation() != null) sentence.setTranslation(request.getTranslation());
        if (request.getExplanation() != null) sentence.setExplanation(request.getExplanation());
        if (request.getDifficultyLevel() != null) sentence.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getChannel() != null) sentence.setChannel(request.getChannel());
        if (request.getTags() != null) sentence.setTags(request.getTags());
        if (request.getLang() != null) sentence.setLang(request.getLang());
        if (request.getDisplayOrder() != null) sentence.setDisplayOrder(request.getDisplayOrder());
        if (request.getTerm() != null) sentence.setTerm(request.getTerm());
        if (request.getWeek() != null) sentence.setWeek(request.getWeek());
        if (request.getIsActive() != null) sentence.setIsActive(request.getIsActive());

        if (request.getPhoneticAudioFile() != null) {
            handleAudioUpload(
                    sentence,
                    request.getPhoneticAudioFile(),
                    request.getPhoneticAudioFilename(),
                    sentence::setPhoneticAudioFilename,
                    "phonetic"
            );
        } else if (request.getPhoneticAudioFilename() != null) {
            sentence.setPhoneticAudioFilename(request.getPhoneticAudioFilename());
        }

        sentence.setUpdatedBy(updatedBy);
        return toResponse(sentenceRepository.save(sentence));
    }

    public void deleteSentence(String id, String updatedBy) {
        Sentence sentence = sentenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sentence", "id", id));
        sentence.setIsActive(false);
        sentence.setUpdatedBy(updatedBy);
        sentenceRepository.save(sentence);
    }

    public void permanentlyDeleteSentence(String id) {
        Sentence sentence = sentenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sentence", "id", id));
        sentenceRepository.delete(sentence);
    }

    @Transactional(readOnly = true)
    public SentenceResponse getSentenceById(String id) {
        return sentenceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Sentence", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<SentenceResponse> searchSentences(String name, Sentence.Language lang, String tags, String channel,
                                                Boolean isActive, Integer term, Integer week,
                                                String difficultyLevel, Pageable pageable) {
        return sentenceRepository.search(name, lang, tags, channel, isActive, term, week, difficultyLevel, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<SentenceResponse> getSentencesByLanguageAndChannel(Sentence.Language lang, String channel) {
        String resolvedChannel = StringUtils.hasText(channel) ? channel : "All";
        return sentenceRepository.findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(resolvedChannel, lang)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public java.io.File getAudioFile(String filename) {
        Path filePath = CmsUtil.resolveSecurePath(sentenceUploadProperties.getAudioDirectory(), filename);
        return filePath.toFile();
    }

    private void handleAudioUpload(Sentence sentence, MultipartFile file, String providedFilename,
                                   Consumer<String> setFilename, String variant) {
        if (file == null || file.isEmpty()) {
            if (StringUtils.hasText(providedFilename)) {
                setFilename.accept(providedFilename);
            }
            return;
        }

        String audioDir = sentenceUploadProperties.getAudioDirectory();
        String baseName = StringUtils.hasText(providedFilename) ? providedFilename : sentence.getName();

        // Clean the string so it's a safe filename (limit length if too long)
        baseName = baseName.trim().replaceAll("[^a-zA-Z0-9\\s-]", "");
        baseName = baseName.replaceAll("\\s+", "-").toLowerCase();
        if (baseName.length() > 40) {
            baseName = baseName.substring(0, 40);
        }
        if (!baseName.endsWith("-" + variant) && !baseName.contains("-" + variant + ".")) {
            baseName = baseName + "-" + variant;
        }

        String ext = getFileExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(ext)) ext = "mp3";
        String filename = baseName.endsWith("." + ext) ? baseName : baseName + "." + ext;

        try {
            Files.createDirectories(Path.of(audioDir));
            Path targetPath = CmsUtil.resolveSecurePath(audioDir, filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            setFilename.accept(filename);
        } catch (IOException e) {
            throw new BusinessException("Failed to save audio file: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    private SentenceResponse toResponse(Sentence sentence) {
        return SentenceResponse.from(sentence, cmsProperties.getBaseUrl());
    }
}
