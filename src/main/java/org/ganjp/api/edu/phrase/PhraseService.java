package org.ganjp.api.edu.phrase;

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
public class PhraseService {

    private final PhraseRepository phraseRepository;
    private final PhraseUploadProperties phraseUploadProperties;
    private final CmsProperties cmsProperties;

    public PhraseResponse createPhrase(PhraseCreateRequest request, String createdBy) {
        String channel = StringUtils.hasText(request.getChannel()) ? request.getChannel() : "All";
        Phrase.Language lang = request.getLang() != null ? request.getLang() : Phrase.Language.EN;

        if (phraseRepository.existsByNameAndChannelAndLang(request.getName(), channel, lang)) {
            throw new BusinessException(String.format("Phrase '%s' already exists for channel '%s' and language '%s'",
                    request.getName(), channel, lang));
        }

        Phrase phrase = Phrase.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .phonetic(request.getPhonetic())
                .synonyms(request.getSynonyms())
                .translation(request.getTranslation())
                .meaningClue(request.getMeaningClue())
                .meaning(request.getMeaning())
                .easyMeaning(request.getEasyMeaning())
                .sentenceOne(request.getSentenceOne())
                .sentenceTwo(request.getSentenceTwo())
                .difficultyLevel(request.getDifficultyLevel())
                .dictionaryUrl(request.getDictionaryUrl())
                .term(request.getTerm())
                .week(request.getWeek())
                .channel(channel)
                .tags(request.getTags())
                .lang(lang)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        handleAudioUpload(
                phrase,
                request.getPhoneticAudioFile(),
                request.getPhoneticAudioOriginalUrl(),
                request.getPhoneticAudioFilename(),
                phrase.getPhoneticAudioOriginalUrl(),
                phrase::setPhoneticAudioFilename,
                phrase::setPhoneticAudioOriginalUrl,
                "phonetic"
        );
        phrase.setCreatedBy(createdBy);
        phrase.setUpdatedBy(createdBy);

        return toResponse(phraseRepository.save(phrase));
    }

    public PhraseResponse updatePhrase(String id, PhraseUpdateRequest request, String updatedBy) {
        Phrase phrase = phraseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phrase", "id", id));

        String name = request.getName() != null ? request.getName() : phrase.getName();
        String channel = request.getChannel() != null ? request.getChannel() : phrase.getChannel();
        Phrase.Language lang = request.getLang() != null ? request.getLang() : phrase.getLang();

        if ((request.getName() != null || request.getChannel() != null || request.getLang() != null)
                && phraseRepository.existsByNameAndChannelAndLangExcludingId(name, channel, lang, id)) {
            throw new BusinessException(String.format("Phrase '%s' already exists for channel '%s' and language '%s'",
                    name, channel, lang));
        }

        if (request.getName() != null) phrase.setName(request.getName());
        if (request.getPhonetic() != null) phrase.setPhonetic(request.getPhonetic());
        if (request.getSynonyms() != null) phrase.setSynonyms(request.getSynonyms());
        if (request.getTranslation() != null) phrase.setTranslation(request.getTranslation());
        if (request.getMeaningClue() != null) phrase.setMeaningClue(request.getMeaningClue());
        if (request.getMeaning() != null) phrase.setMeaning(request.getMeaning());
        if (request.getEasyMeaning() != null) phrase.setEasyMeaning(request.getEasyMeaning());
        if (request.getSentenceOne() != null) phrase.setSentenceOne(request.getSentenceOne());
        if (request.getSentenceTwo() != null) phrase.setSentenceTwo(request.getSentenceTwo());
        if (request.getDifficultyLevel() != null) phrase.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getDictionaryUrl() != null) phrase.setDictionaryUrl(request.getDictionaryUrl());
        if (request.getTerm() != null) phrase.setTerm(request.getTerm());
        if (request.getWeek() != null) phrase.setWeek(request.getWeek());
        if (request.getChannel() != null) phrase.setChannel(request.getChannel());
        if (request.getTags() != null) phrase.setTags(request.getTags());
        if (request.getLang() != null) phrase.setLang(request.getLang());
        if (request.getDisplayOrder() != null) phrase.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) phrase.setIsActive(request.getIsActive());

        if (request.getPhoneticAudioFile() != null || request.getPhoneticAudioOriginalUrl() != null) {
            handleAudioUpload(
                    phrase,
                    request.getPhoneticAudioFile(),
                    request.getPhoneticAudioOriginalUrl(),
                    request.getPhoneticAudioFilename(),
                    phrase.getPhoneticAudioOriginalUrl(),
                    phrase::setPhoneticAudioFilename,
                    phrase::setPhoneticAudioOriginalUrl,
                    "phonetic"
            );
        } else if (request.getPhoneticAudioFilename() != null) {
            phrase.setPhoneticAudioFilename(request.getPhoneticAudioFilename());
        }

        phrase.setUpdatedBy(updatedBy);
        return toResponse(phraseRepository.save(phrase));
    }

    public void deletePhrase(String id, String updatedBy) {
        Phrase phrase = phraseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phrase", "id", id));
        phrase.setIsActive(false);
        phrase.setUpdatedBy(updatedBy);
        phraseRepository.save(phrase);
    }

    public void permanentlyDeletePhrase(String id) {
        Phrase phrase = phraseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phrase", "id", id));
        phraseRepository.delete(phrase);
    }

    @Transactional(readOnly = true)
    public PhraseResponse getPhraseById(String id) {
        return phraseRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Phrase", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<PhraseResponse> searchPhrases(String name, Phrase.Language lang, String tags, String channel,
                                              Boolean isActive, Integer term, Integer week,
                                              String difficultyLevel, Pageable pageable) {
        return phraseRepository.search(name, lang, tags, channel, isActive, term, week, difficultyLevel, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PhraseResponse> getPhrasesByLanguageAndChannel(Phrase.Language lang, String channel) {
        String resolvedChannel = StringUtils.hasText(channel) ? channel : "All";
        return phraseRepository.findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(resolvedChannel, lang)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public java.io.File getAudioFile(String filename) {
        Path filePath = CmsUtil.resolveSecurePath(phraseUploadProperties.getAudioDirectory(), filename);
        return filePath.toFile();
    }

    private void handleAudioUpload(Phrase phrase, MultipartFile file, String originalUrl, String providedFilename,
                                   String currentOriginalUrl, Consumer<String> setFilename,
                                   Consumer<String> setOriginalUrl, String variant) {
        String audioDir = phraseUploadProperties.getAudioDirectory();
        String baseName = StringUtils.hasText(providedFilename) ? providedFilename : phrase.getName();
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
                    log.error("Failed to download phrase audio from URL: {}", originalUrl, e);
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

    private PhraseResponse toResponse(Phrase phrase) {
        return PhraseResponse.from(phrase, cmsProperties.getBaseUrl());
    }
}
