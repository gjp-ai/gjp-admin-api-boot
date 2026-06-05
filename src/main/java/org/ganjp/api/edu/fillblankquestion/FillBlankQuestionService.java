package org.ganjp.api.edu.fillblankquestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.common.exception.BusinessException;
import org.ganjp.api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FillBlankQuestionService {

    private final FillBlankQuestionRepository fillBlankQuestionRepository;

    public FillBlankQuestionResponse createFillBlankQuestion(FillBlankQuestionCreateRequest request, String createdBy) {
        String channel = StringUtils.hasText(request.getChannel()) ? request.getChannel() : "All";
        FillBlankQuestion.Language lang = request.getLang() != null ? request.getLang() : FillBlankQuestion.Language.EN;

        if (fillBlankQuestionRepository.existsByQuestionAndChannelAndLang(request.getQuestion(), channel, lang)) {
            throw new BusinessException(String.format("Fill blank question already exists for channel '%s' and language '%s'", channel, lang));
        }

        FillBlankQuestion question = FillBlankQuestion.builder()
                .id(UUID.randomUUID().toString())
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .explanation(request.getExplanation())
                .difficultyLevel(request.getDifficultyLevel())
                .gradeLevel(request.getGradeLevel())
                .subject(request.getSubject())
                .topic(request.getTopic())
                .term(request.getTerm())
                .week(request.getWeek())
                .channel(channel)
                .tags(request.getTags())
                .lang(lang)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        question.setCreatedBy(createdBy);
        question.setUpdatedBy(createdBy);

        return FillBlankQuestionResponse.from(fillBlankQuestionRepository.save(question));
    }

    public FillBlankQuestionResponse updateFillBlankQuestion(String id, FillBlankQuestionUpdateRequest request, String updatedBy) {
        FillBlankQuestion question = findEntity(id);

        String nextQuestion = request.getQuestion() != null ? request.getQuestion() : question.getQuestion();
        String nextChannel = request.getChannel() != null ? request.getChannel() : question.getChannel();
        FillBlankQuestion.Language nextLang = request.getLang() != null ? request.getLang() : question.getLang();

        if ((request.getQuestion() != null || request.getChannel() != null || request.getLang() != null)
                && fillBlankQuestionRepository.existsByQuestionAndChannelAndLangExcludingId(nextQuestion, nextChannel, nextLang, id)) {
            throw new BusinessException(String.format("Fill blank question already exists for channel '%s' and language '%s'", nextChannel, nextLang));
        }

        if (request.getQuestion() != null) question.setQuestion(request.getQuestion());
        if (request.getAnswer() != null) question.setAnswer(request.getAnswer());
        if (request.getExplanation() != null) question.setExplanation(request.getExplanation());
        if (request.getDifficultyLevel() != null) question.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getGradeLevel() != null) question.setGradeLevel(request.getGradeLevel());
        if (request.getSubject() != null) question.setSubject(request.getSubject());
        if (request.getTopic() != null) question.setTopic(request.getTopic());
        if (request.getTerm() != null) question.setTerm(request.getTerm());
        if (request.getWeek() != null) question.setWeek(request.getWeek());
        if (request.getChannel() != null) question.setChannel(request.getChannel());
        if (request.getTags() != null) question.setTags(request.getTags());
        if (request.getLang() != null) question.setLang(request.getLang());
        if (request.getDisplayOrder() != null) question.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) question.setIsActive(request.getIsActive());
        question.setUpdatedBy(updatedBy);

        return FillBlankQuestionResponse.from(fillBlankQuestionRepository.save(question));
    }

    public void deleteFillBlankQuestion(String id, String updatedBy) {
        FillBlankQuestion question = findEntity(id);
        question.setIsActive(false);
        question.setUpdatedBy(updatedBy);
        fillBlankQuestionRepository.save(question);
    }

    public void permanentlyDeleteFillBlankQuestion(String id) {
        fillBlankQuestionRepository.delete(findEntity(id));
    }

    @Transactional(readOnly = true)
    public FillBlankQuestionResponse getFillBlankQuestionById(String id) {
        return FillBlankQuestionResponse.from(findEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<FillBlankQuestionResponse> searchFillBlankQuestions(String question, FillBlankQuestion.Language lang,
                                                                    String tags, String channel, Boolean isActive,
                                                                    Integer term, Integer week, String difficultyLevel,
                                                                    String gradeLevel, String subject, String topic,
                                                                    Pageable pageable) {
        return fillBlankQuestionRepository.search(question, lang, tags, channel, isActive, term, week,
                        difficultyLevel, gradeLevel, subject, topic, pageable)
                .map(FillBlankQuestionResponse::from);
    }

    @Transactional(readOnly = true)
    public List<FillBlankQuestionResponse> getFillBlankQuestionsByLanguageAndChannel(FillBlankQuestion.Language lang, String channel) {
        String resolvedChannel = StringUtils.hasText(channel) ? channel : "All";
        return fillBlankQuestionRepository.findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(resolvedChannel, lang)
                .stream()
                .map(FillBlankQuestionResponse::from)
                .toList();
    }

    public void incrementSuccessCount(String id) {
        FillBlankQuestion question = findEntity(id);
        question.setSuccessCount(question.getSuccessCount() + 1);
        fillBlankQuestionRepository.save(question);
    }

    public void incrementFailCount(String id) {
        FillBlankQuestion question = findEntity(id);
        question.setFailCount(question.getFailCount() + 1);
        fillBlankQuestionRepository.save(question);
    }

    private FillBlankQuestion findEntity(String id) {
        return fillBlankQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FillBlankQuestion", "id", id));
    }
}
