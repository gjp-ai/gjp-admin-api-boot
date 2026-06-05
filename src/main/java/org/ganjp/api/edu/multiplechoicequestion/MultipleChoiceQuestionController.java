package org.ganjp.api.edu.multiplechoicequestion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.auth.security.JwtUtils;
import org.ganjp.api.common.model.ApiResponse;
import org.ganjp.api.common.model.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/edu-multiple-choice-questions")
@RequiredArgsConstructor
public class MultipleChoiceQuestionController {

    private final MultipleChoiceQuestionService multipleChoiceQuestionService;
    private final JwtUtils jwtUtils;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<MultipleChoiceQuestionResponse>>> searchMultipleChoiceQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String question,
            @RequestParam(required = false) MultipleChoiceQuestion.Language lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String topic
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, sortDirection, sort);
        Page<MultipleChoiceQuestionResponse> questions = multipleChoiceQuestionService.searchMultipleChoiceQuestions(
                question, lang, tags, channel, isActive, term, week, difficultyLevel, gradeLevel, subject, topic, pageable);
        PaginatedResponse<MultipleChoiceQuestionResponse> response = PaginatedResponse.of(
                questions.getContent(), questions.getNumber(), questions.getSize(), questions.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(response, "Multiple choice questions found"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MultipleChoiceQuestionResponse>> getMultipleChoiceQuestion(@PathVariable String id) {
        MultipleChoiceQuestionResponse response = multipleChoiceQuestionService.getMultipleChoiceQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Multiple choice question found"));
    }

    @GetMapping("/by-language/{lang}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<MultipleChoiceQuestionResponse>>> getMultipleChoiceQuestionsByLanguage(
            @PathVariable MultipleChoiceQuestion.Language lang,
            @RequestParam(defaultValue = "All") String channel
    ) {
        List<MultipleChoiceQuestionResponse> response = multipleChoiceQuestionService.getMultipleChoiceQuestionsByLanguageAndChannel(lang, channel);
        return ResponseEntity.ok(ApiResponse.success(response, "Multiple choice questions found"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MultipleChoiceQuestionResponse>> createMultipleChoiceQuestion(
            @Valid @RequestBody MultipleChoiceQuestionCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        MultipleChoiceQuestionResponse response = multipleChoiceQuestionService.createMultipleChoiceQuestion(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Multiple choice question created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MultipleChoiceQuestionResponse>> updateMultipleChoiceQuestion(
            @PathVariable String id,
            @Valid @RequestBody MultipleChoiceQuestionUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        MultipleChoiceQuestionResponse response = multipleChoiceQuestionService.updateMultipleChoiceQuestion(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Multiple choice question updated"));
    }

    @PostMapping("/{id}/success-count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> incrementSuccessCount(@PathVariable String id) {
        multipleChoiceQuestionService.incrementSuccessCount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Multiple choice question success count incremented"));
    }

    @PostMapping("/{id}/fail-count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> incrementFailCount(@PathVariable String id) {
        multipleChoiceQuestionService.incrementFailCount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Multiple choice question fail count incremented"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMultipleChoiceQuestion(@PathVariable String id, HttpServletRequest httpRequest) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        multipleChoiceQuestionService.deleteMultipleChoiceQuestion(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Multiple choice question deleted"));
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteMultipleChoiceQuestion(@PathVariable String id) {
        multipleChoiceQuestionService.permanentlyDeleteMultipleChoiceQuestion(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Multiple choice question permanently deleted"));
    }
}
