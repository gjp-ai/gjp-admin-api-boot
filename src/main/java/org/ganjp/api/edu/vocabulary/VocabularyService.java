package org.ganjp.api.edu.vocabulary;

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
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final VocabularyUploadProperties vocabularyUploadProperties;
    private final CmsProperties cmsProperties;

    public VocabularyResponse createVocabulary(VocabularyCreateRequest request, String createdBy) {
        String channel = StringUtils.hasText(request.getChannel()) ? request.getChannel() : "All";
        Vocabulary.Language lang = request.getLang() != null ? request.getLang() : Vocabulary.Language.EN;

        if (vocabularyRepository.existsByNameAndChannelAndLang(request.getName(), channel, lang)) {
            throw new BusinessException(String.format("Vocabulary '%s' already exists for channel '%s' and language '%s'",
                    request.getName(), channel, lang));
        }

        Vocabulary vocabulary = Vocabulary.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .phoneticUs(request.getPhoneticUs())
                .phoneticUk(request.getPhoneticUk())
                .partOfSpeech(request.getPartOfSpeech())
                .synonyms(request.getSynonyms())
                .translation(request.getTranslation())
                .meaningClue(request.getMeaningClue())
                .meaning(request.getMeaning())
                .easyMeaning(request.getEasyMeaning())
                .sentenceOne(request.getSentenceOne())
                .sentenceTwo(request.getSentenceTwo())
                .difficultyLevel(request.getDifficultyLevel())
                .dictionaryUrl(request.getDictionaryUrl())
                .additionalInfo(request.getAdditionalInfo())
                .term(request.getTerm())
                .week(request.getWeek())
                .channel(channel)
                .tags(request.getTags())
                .lang(lang)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        handleAudioUpload(
                vocabulary,
                request.getPhoneticUsAudioFile(),
                request.getPhoneticUsAudioOriginalUrl(),
                request.getPhoneticUsAudioFilename(),
                vocabulary.getPhoneticUsAudioOriginalUrl(),
                vocabulary::setPhoneticUsAudioFilename,
                vocabulary::setPhoneticUsAudioOriginalUrl,
                "us"
        );
        handleAudioUpload(
                vocabulary,
                request.getPhoneticUkAudioFile(),
                request.getPhoneticUkAudioOriginalUrl(),
                request.getPhoneticUkAudioFilename(),
                vocabulary.getPhoneticUkAudioOriginalUrl(),
                vocabulary::setPhoneticUkAudioFilename,
                vocabulary::setPhoneticUkAudioOriginalUrl,
                "uk"
        );
        vocabulary.setCreatedBy(createdBy);
        vocabulary.setUpdatedBy(createdBy);

        return toResponse(vocabularyRepository.save(vocabulary));
    }

    public VocabularyResponse updateVocabulary(String id, VocabularyUpdateRequest request, String updatedBy) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary", "id", id));

        String name = request.getName() != null ? request.getName() : vocabulary.getName();
        String channel = request.getChannel() != null ? request.getChannel() : vocabulary.getChannel();
        Vocabulary.Language lang = request.getLang() != null ? request.getLang() : vocabulary.getLang();

        if ((request.getName() != null || request.getChannel() != null || request.getLang() != null)
                && vocabularyRepository.existsByNameAndChannelAndLangExcludingId(name, channel, lang, id)) {
            throw new BusinessException(String.format("Vocabulary '%s' already exists for channel '%s' and language '%s'",
                    name, channel, lang));
        }

        if (request.getName() != null) vocabulary.setName(request.getName());
        if (request.getPhoneticUs() != null) vocabulary.setPhoneticUs(request.getPhoneticUs());
        if (request.getPhoneticUk() != null) vocabulary.setPhoneticUk(request.getPhoneticUk());
        if (request.getPartOfSpeech() != null) vocabulary.setPartOfSpeech(request.getPartOfSpeech());
        if (request.getSynonyms() != null) vocabulary.setSynonyms(request.getSynonyms());
        if (request.getTranslation() != null) vocabulary.setTranslation(request.getTranslation());
        if (request.getMeaningClue() != null) vocabulary.setMeaningClue(request.getMeaningClue());
        if (request.getMeaning() != null) vocabulary.setMeaning(request.getMeaning());
        if (request.getEasyMeaning() != null) vocabulary.setEasyMeaning(request.getEasyMeaning());
        if (request.getSentenceOne() != null) vocabulary.setSentenceOne(request.getSentenceOne());
        if (request.getSentenceTwo() != null) vocabulary.setSentenceTwo(request.getSentenceTwo());
        if (request.getDifficultyLevel() != null) vocabulary.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getDictionaryUrl() != null) vocabulary.setDictionaryUrl(request.getDictionaryUrl());
        if (request.getAdditionalInfo() != null) vocabulary.setAdditionalInfo(request.getAdditionalInfo());
        if (request.getTerm() != null) vocabulary.setTerm(request.getTerm());
        if (request.getWeek() != null) vocabulary.setWeek(request.getWeek());
        if (request.getChannel() != null) vocabulary.setChannel(request.getChannel());
        if (request.getTags() != null) vocabulary.setTags(request.getTags());
        if (request.getLang() != null) vocabulary.setLang(request.getLang());
        if (request.getDisplayOrder() != null) vocabulary.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) vocabulary.setIsActive(request.getIsActive());

        if (request.getPhoneticUsAudioFile() != null || request.getPhoneticUsAudioOriginalUrl() != null) {
            handleAudioUpload(
                    vocabulary,
                    request.getPhoneticUsAudioFile(),
                    request.getPhoneticUsAudioOriginalUrl(),
                    request.getPhoneticUsAudioFilename(),
                    vocabulary.getPhoneticUsAudioOriginalUrl(),
                    vocabulary::setPhoneticUsAudioFilename,
                    vocabulary::setPhoneticUsAudioOriginalUrl,
                    "us"
            );
        } else if (request.getPhoneticUsAudioFilename() != null) {
            vocabulary.setPhoneticUsAudioFilename(request.getPhoneticUsAudioFilename());
        }

        if (request.getPhoneticUkAudioFile() != null || request.getPhoneticUkAudioOriginalUrl() != null) {
            handleAudioUpload(
                    vocabulary,
                    request.getPhoneticUkAudioFile(),
                    request.getPhoneticUkAudioOriginalUrl(),
                    request.getPhoneticUkAudioFilename(),
                    vocabulary.getPhoneticUkAudioOriginalUrl(),
                    vocabulary::setPhoneticUkAudioFilename,
                    vocabulary::setPhoneticUkAudioOriginalUrl,
                    "uk"
            );
        } else if (request.getPhoneticUkAudioFilename() != null) {
            vocabulary.setPhoneticUkAudioFilename(request.getPhoneticUkAudioFilename());
        }

        vocabulary.setUpdatedBy(updatedBy);
        return toResponse(vocabularyRepository.save(vocabulary));
    }

    public void deleteVocabulary(String id, String updatedBy) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary", "id", id));
        vocabulary.setIsActive(false);
        vocabulary.setUpdatedBy(updatedBy);
        vocabularyRepository.save(vocabulary);
    }

    public void permanentlyDeleteVocabulary(String id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary", "id", id));
        vocabularyRepository.delete(vocabulary);
    }

    @Transactional(readOnly = true)
    public VocabularyResponse getVocabularyById(String id) {
        return vocabularyRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<VocabularyResponse> searchVocabularies(String name, Vocabulary.Language lang, String tags, String channel,
                                                       Boolean isActive, Integer term, Integer week,
                                                       String difficultyLevel, String partOfSpeech, Pageable pageable) {
        return vocabularyRepository.search(name, lang, tags, channel, isActive, term, week, difficultyLevel, partOfSpeech, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<VocabularyResponse> getVocabulariesByLanguageAndChannel(Vocabulary.Language lang, String channel) {
        String resolvedChannel = StringUtils.hasText(channel) ? channel : "All";
        return vocabularyRepository.findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(resolvedChannel, lang)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public java.io.File getAudioFile(String filename) {
        Path filePath = CmsUtil.resolveSecurePath(vocabularyUploadProperties.getAudioDirectory(), filename);
        return filePath.toFile();
    }

    private void handleAudioUpload(Vocabulary vocabulary, MultipartFile file, String originalUrl, String providedFilename,
                                   String currentOriginalUrl, Consumer<String> setFilename,
                                   Consumer<String> setOriginalUrl, String variant) {
        String audioDir = vocabularyUploadProperties.getAudioDirectory();
        String baseName = StringUtils.hasText(providedFilename) ? providedFilename : vocabulary.getName();
        baseName = baseName.trim().replaceAll("\\s+", "-").toLowerCase();
        if (!baseName.endsWith("-" + variant) && !baseName.contains("-" + variant + ".")) {
            baseName = baseName + "-" + variant;
        }

        if (file != null && !file.isEmpty()) {
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
        } else if (StringUtils.hasText(originalUrl)) {
            boolean isUrlChanged = !originalUrl.equals(currentOriginalUrl);
            setOriginalUrl.accept(originalUrl);

            if (isUrlChanged && originalUrl.toLowerCase().startsWith("http")) {
                try {
                    java.net.URL url = new java.net.URL(originalUrl);
                    String ext = getFileExtension(url.getPath());
                    if (!StringUtils.hasText(ext)) ext = "mp3";
                    String filename = baseName.endsWith("." + ext) ? baseName : baseName + "." + ext;

                    Files.createDirectories(Path.of(audioDir));
                    Path targetPath = CmsUtil.resolveSecurePath(audioDir, filename);
                    try (java.io.InputStream in = url.openStream()) {
                        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    setFilename.accept(filename);
                } catch (Exception e) {
                    log.error("Failed to download vocabulary audio from URL: {}", originalUrl, e);
                }
            } else if (StringUtils.hasText(providedFilename)) {
                setFilename.accept(providedFilename);
            }
        } else if (StringUtils.hasText(providedFilename)) {
            setFilename.accept(providedFilename);
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

    private VocabularyResponse toResponse(Vocabulary vocabulary) {
        return VocabularyResponse.from(vocabulary, cmsProperties.getBaseUrl());
    }
}
