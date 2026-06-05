package org.ganjp.api.edu.freetextquestion;

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
public class FreeTextQuestionService {

    private final FreeTextQuestionRepository freeTextQuestionRepository;

    public FreeTextQuestionResponse createFreeTextQuestion(FreeTextQuestionCreateRequest request, String createdBy) {
        String channel = StringUtils.hasText(request.getChannel()) ? request.getChannel() : "All";
        FreeTextQuestion.Language lang = request.getLang() != null ? request.getLang() : FreeTextQuestion.Language.EN;

        if (freeTextQuestionRepository.existsByQuestionAndChannelAndLang(request.getQuestion(), channel, lang)) {
            throw new BusinessException(String.format("Free text question already exists for channel '%s' and language '%s'", channel, lang));
        }

        FreeTextQuestion question = FreeTextQuestion.builder()
                .id(UUID.randomUUID().toString())
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .description(request.getDescription())
                .questionA(request.getQuestionA())
                .answerA(request.getAnswerA())
                .questionB(request.getQuestionB())
                .answerB(request.getAnswerB())
                .questionC(request.getQuestionC())
                .answerC(request.getAnswerC())
                .questionD(request.getQuestionD())
                .answerD(request.getAnswerD())
                .questionE(request.getQuestionE())
                .answerE(request.getAnswerE())
                .questionF(request.getQuestionF())
                .answerF(request.getAnswerF())
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

        return FreeTextQuestionResponse.from(freeTextQuestionRepository.save(question));
    }

    public FreeTextQuestionResponse updateFreeTextQuestion(String id, FreeTextQuestionUpdateRequest request, String updatedBy) {
        FreeTextQuestion question = findEntity(id);

        String nextQuestion = request.getQuestion() != null ? request.getQuestion() : question.getQuestion();
        String nextChannel = request.getChannel() != null ? request.getChannel() : question.getChannel();
        FreeTextQuestion.Language nextLang = request.getLang() != null ? request.getLang() : question.getLang();

        if ((request.getQuestion() != null || request.getChannel() != null || request.getLang() != null)
                && freeTextQuestionRepository.existsByQuestionAndChannelAndLangExcludingId(nextQuestion, nextChannel, nextLang, id)) {
            throw new BusinessException(String.format("Free text question already exists for channel '%s' and language '%s'", nextChannel, nextLang));
        }

        if (request.getQuestion() != null) question.setQuestion(request.getQuestion());
        if (request.getAnswer() != null) question.setAnswer(request.getAnswer());
        if (request.getDescription() != null) question.setDescription(request.getDescription());
        if (request.getQuestionA() != null) question.setQuestionA(request.getQuestionA());
        if (request.getAnswerA() != null) question.setAnswerA(request.getAnswerA());
        if (request.getQuestionB() != null) question.setQuestionB(request.getQuestionB());
        if (request.getAnswerB() != null) question.setAnswerB(request.getAnswerB());
        if (request.getQuestionC() != null) question.setQuestionC(request.getQuestionC());
        if (request.getAnswerC() != null) question.setAnswerC(request.getAnswerC());
        if (request.getQuestionD() != null) question.setQuestionD(request.getQuestionD());
        if (request.getAnswerD() != null) question.setAnswerD(request.getAnswerD());
        if (request.getQuestionE() != null) question.setQuestionE(request.getQuestionE());
        if (request.getAnswerE() != null) question.setAnswerE(request.getAnswerE());
        if (request.getQuestionF() != null) question.setQuestionF(request.getQuestionF());
        if (request.getAnswerF() != null) question.setAnswerF(request.getAnswerF());
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

        return FreeTextQuestionResponse.from(freeTextQuestionRepository.save(question));
    }

    public void deleteFreeTextQuestion(String id, String updatedBy) {
        FreeTextQuestion question = findEntity(id);
        question.setIsActive(false);
        question.setUpdatedBy(updatedBy);
        freeTextQuestionRepository.save(question);
    }

    public void permanentlyDeleteFreeTextQuestion(String id) {
        freeTextQuestionRepository.delete(findEntity(id));
    }

    @Transactional(readOnly = true)
    public FreeTextQuestionResponse getFreeTextQuestionById(String id) {
        return FreeTextQuestionResponse.from(findEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<FreeTextQuestionResponse> searchFreeTextQuestions(String question, FreeTextQuestion.Language lang,
                                                                  String tags, String channel, Boolean isActive,
                                                                  Integer term, Integer week, String difficultyLevel,
                                                                  String gradeLevel, String subject, String topic,
                                                                  Pageable pageable) {
        return freeTextQuestionRepository.search(question, lang, tags, channel, isActive, term, week,
                        difficultyLevel, gradeLevel, subject, topic, pageable)
                .map(FreeTextQuestionResponse::from);
    }

    @Transactional(readOnly = true)
    public List<FreeTextQuestionResponse> getFreeTextQuestionsByLanguageAndChannel(FreeTextQuestion.Language lang, String channel) {
        String resolvedChannel = StringUtils.hasText(channel) ? channel : "All";
        return freeTextQuestionRepository.findByChannelAndLangAndIsActiveTrueOrderByDisplayOrderAsc(resolvedChannel, lang)
                .stream()
                .map(FreeTextQuestionResponse::from)
                .toList();
    }

    public void incrementSuccessCount(String id) {
        FreeTextQuestion question = findEntity(id);
        question.setSuccessCount(question.getSuccessCount() + 1);
        freeTextQuestionRepository.save(question);
    }

    public void incrementFailCount(String id) {
        FreeTextQuestion question = findEntity(id);
        question.setFailCount(question.getFailCount() + 1);
        freeTextQuestionRepository.save(question);
    }

    private FreeTextQuestion findEntity(String id) {
        return freeTextQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FreeTextQuestion", "id", id));
    }
}
