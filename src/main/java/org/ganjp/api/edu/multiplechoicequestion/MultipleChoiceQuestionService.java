package org.ganjp.api.edu.multiplechoicequestion;

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
public class MultipleChoiceQuestionService {

    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;

    public MultipleChoiceQuestionResponse createMultipleChoiceQuestion(MultipleChoiceQuestionCreateRequest request, String createdBy) {
        String channel = StringUtils.hasText(request.getChannel()) ? request.getChannel() : "All";
        MultipleChoiceQuestion.Language lang = request.getLang() != null ? request.getLang() : MultipleChoiceQuestion.Language.EN;

        if (multipleChoiceQuestionRepository.existsByQuestionAndChannelAndLang(request.getQuestion(), channel, lang)) {
            throw new BusinessException(String.format("Multiple choice question already exists for channel '%s' and language '%s'", channel, lang));
        }

        MultipleChoiceQuestion question = MultipleChoiceQuestion.builder()
                .id(UUID.randomUUID().toString())
                .question(request.getQuestion())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
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

        return MultipleChoiceQuestionResponse.from(multipleChoiceQuestionRepository.save(question));
    }

    public MultipleChoiceQuestionResponse updateMultipleChoiceQuestion(String id, MultipleChoiceQuestionUpdateRequest request, String updatedBy) {
        MultipleChoiceQuestion question = findEntity(id);

        String nextQuestion = request.getQuestion() != null ? request.getQuestion() : question.getQuestion();
        String nextChannel = request.getChannel() != null ? request.getChannel() : question.getChannel();
        MultipleChoiceQuestion.Language nextLang = request.getLang() != null ? request.getLang() : question.getLang();

        if ((request.getQuestion() != null || request.getChannel() != null || request.getLang() != null)
                && multipleChoiceQuestionRepository.existsByQuestionAndChannelAndLangExcludingId(nextQuestion, nextChannel, nextLang, id)) {
            throw new BusinessException(String.format("Multiple choice question already exists for channel '%s' and language '%s'", nextChannel, nextLang));
        }

        if (request.getQuestion() != null) question.setQuestion(request.getQuestion());
        if (request.getOptionA() != null) question.setOptionA(request.getOptionA());
        if (request.getOptionB() != null) question.setOptionB(request.getOptionB());
        if (request.getOptionC() != null) question.setOptionC(request.getOptionC());
        if (request.getOptionD() != null) question.setOptionD(request.getOptionD());
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

        return MultipleChoiceQuestionResponse.from(multipleChoiceQuestionRepository.save(question));
    }

    public void deleteMultipleChoiceQuestion(String id, String updatedBy) {
        MultipleChoiceQuestion question = findEntity(id);
        question.setIsActive(false);
        question.setUpdatedBy(updatedBy);
        multipleChoiceQuestionRepository.save(question);
    }

    public void permanentlyDeleteMultipleChoiceQuestion(String id) {
        multipleChoiceQuestionRepository.delete(findEntity(id));
    }

    @Transactional(readOnly = true)
    public MultipleChoiceQuestionResponse getMultipleChoiceQuestionById(String id) {
        return MultipleChoiceQuestionResponse.from(findEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<MultipleChoiceQuestionResponse> searchMultipleChoiceQuestions(String question, MultipleChoiceQuestion.Language lang,
                                                                              String tags, String channel, Boolean isActive,
                                                                              Integer term, Integer week, String difficultyLevel,
                                                                              String gradeLevel, String subject, String topic,
                                                                              Pageable pageable) {
        return multipleChoiceQuestionRepository.search(question, lang, tags, channel, isActive, term, week,
                        difficultyLevel, gradeLevel, subject, topic, pageable)
                .map(MultipleChoiceQuestionResponse::from);
    }

    @Transactional(readOnly = true)
    public List<MultipleChoiceQuestionResponse> getMultipleChoiceQuestionsByLanguageAndChannel(MultipleChoiceQuestion.Language lang, String channel) {
        String resolvedChannel = StringUtils.hasText(channel) ? channel : "All";
        return multipleChoiceQuestionRepository.findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(resolvedChannel, lang)
                .stream()
                .map(MultipleChoiceQuestionResponse::from)
                .toList();
    }

    public void incrementSuccessCount(String id) {
        MultipleChoiceQuestion question = findEntity(id);
        question.setSuccessCount(question.getSuccessCount() + 1);
        multipleChoiceQuestionRepository.save(question);
    }

    public void incrementFailCount(String id) {
        MultipleChoiceQuestion question = findEntity(id);
        question.setFailCount(question.getFailCount() + 1);
        multipleChoiceQuestionRepository.save(question);
    }

    private MultipleChoiceQuestion findEntity(String id) {
        return multipleChoiceQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MultipleChoiceQuestion", "id", id));
    }
}
