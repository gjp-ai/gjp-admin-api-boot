package org.ganjp.api.edu.truefalsequestion;

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
public class TrueFalseQuestionService {

    private final TrueFalseQuestionRepository trueFalseQuestionRepository;

    public TrueFalseQuestionResponse createTrueFalseQuestion(TrueFalseQuestionCreateRequest request, String createdBy) {
        String channel = StringUtils.hasText(request.getChannel()) ? request.getChannel() : "All";
        TrueFalseQuestion.Language lang = request.getLang() != null ? request.getLang() : TrueFalseQuestion.Language.EN;

        if (trueFalseQuestionRepository.existsByQuestionAndChannelAndLang(request.getQuestion(), channel, lang)) {
            throw new BusinessException(String.format("True/false question already exists for channel '%s' and language '%s'", channel, lang));
        }

        TrueFalseQuestion question = TrueFalseQuestion.builder()
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

        return TrueFalseQuestionResponse.from(trueFalseQuestionRepository.save(question));
    }

    public TrueFalseQuestionResponse updateTrueFalseQuestion(String id, TrueFalseQuestionUpdateRequest request, String updatedBy) {
        TrueFalseQuestion question = findEntity(id);

        String nextQuestion = request.getQuestion() != null ? request.getQuestion() : question.getQuestion();
        String nextChannel = request.getChannel() != null ? request.getChannel() : question.getChannel();
        TrueFalseQuestion.Language nextLang = request.getLang() != null ? request.getLang() : question.getLang();

        if ((request.getQuestion() != null || request.getChannel() != null || request.getLang() != null)
                && trueFalseQuestionRepository.existsByQuestionAndChannelAndLangExcludingId(nextQuestion, nextChannel, nextLang, id)) {
            throw new BusinessException(String.format("True/false question already exists for channel '%s' and language '%s'", nextChannel, nextLang));
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

        return TrueFalseQuestionResponse.from(trueFalseQuestionRepository.save(question));
    }

    public void deleteTrueFalseQuestion(String id, String updatedBy) {
        TrueFalseQuestion question = findEntity(id);
        question.setIsActive(false);
        question.setUpdatedBy(updatedBy);
        trueFalseQuestionRepository.save(question);
    }

    public void permanentlyDeleteTrueFalseQuestion(String id) {
        trueFalseQuestionRepository.delete(findEntity(id));
    }

    @Transactional(readOnly = true)
    public TrueFalseQuestionResponse getTrueFalseQuestionById(String id) {
        return TrueFalseQuestionResponse.from(findEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<TrueFalseQuestionResponse> searchTrueFalseQuestions(String question, TrueFalseQuestion.Language lang,
                                                                    String tags, String channel, Boolean isActive,
                                                                    Integer term, Integer week, String difficultyLevel,
                                                                    String gradeLevel, String subject, String topic,
                                                                    Pageable pageable) {
        return trueFalseQuestionRepository.search(question, lang, tags, channel, isActive, term, week,
                        difficultyLevel, gradeLevel, subject, topic, pageable)
                .map(TrueFalseQuestionResponse::from);
    }

    @Transactional(readOnly = true)
    public List<TrueFalseQuestionResponse> getTrueFalseQuestionsByLanguageAndChannel(TrueFalseQuestion.Language lang, String channel) {
        String resolvedChannel = StringUtils.hasText(channel) ? channel : "All";
        return trueFalseQuestionRepository.findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(resolvedChannel, lang)
                .stream()
                .map(TrueFalseQuestionResponse::from)
                .toList();
    }

    public void incrementSuccessCount(String id) {
        TrueFalseQuestion question = findEntity(id);
        question.setSuccessCount(question.getSuccessCount() + 1);
        trueFalseQuestionRepository.save(question);
    }

    public void incrementFailCount(String id) {
        TrueFalseQuestion question = findEntity(id);
        question.setFailCount(question.getFailCount() + 1);
        trueFalseQuestionRepository.save(question);
    }

    private TrueFalseQuestion findEntity(String id) {
        return trueFalseQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrueFalseQuestion", "id", id));
    }
}
