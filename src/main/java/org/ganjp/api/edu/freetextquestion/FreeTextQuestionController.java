package org.ganjp.api.edu.freetextquestion;

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
@RequestMapping("/v1/edu-free-text-questions")
@RequiredArgsConstructor
public class FreeTextQuestionController {

    private final FreeTextQuestionService freeTextQuestionService;
    private final JwtUtils jwtUtils;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<FreeTextQuestionResponse>>> searchFreeTextQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String question,
            @RequestParam(required = false) FreeTextQuestion.Language lang,
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
        Page<FreeTextQuestionResponse> questions = freeTextQuestionService.searchFreeTextQuestions(
                question, lang, tags, channel, isActive, term, week, difficultyLevel, gradeLevel, subject, topic, pageable);
        PaginatedResponse<FreeTextQuestionResponse> response = PaginatedResponse.of(
                questions.getContent(), questions.getNumber(), questions.getSize(), questions.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(response, "Free text questions found"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FreeTextQuestionResponse>> getFreeTextQuestion(@PathVariable String id) {
        FreeTextQuestionResponse response = freeTextQuestionService.getFreeTextQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Free text question found"));
    }

    @GetMapping("/by-language/{lang}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<FreeTextQuestionResponse>>> getFreeTextQuestionsByLanguage(
            @PathVariable FreeTextQuestion.Language lang,
            @RequestParam(defaultValue = "All") String channel
    ) {
        List<FreeTextQuestionResponse> response = freeTextQuestionService.getFreeTextQuestionsByLanguageAndChannel(lang, channel);
        return ResponseEntity.ok(ApiResponse.success(response, "Free text questions found"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FreeTextQuestionResponse>> createFreeTextQuestion(
            @Valid @RequestBody FreeTextQuestionCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        FreeTextQuestionResponse response = freeTextQuestionService.createFreeTextQuestion(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Free text question created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FreeTextQuestionResponse>> updateFreeTextQuestion(
            @PathVariable String id,
            @Valid @RequestBody FreeTextQuestionUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        FreeTextQuestionResponse response = freeTextQuestionService.updateFreeTextQuestion(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Free text question updated"));
    }

    @PostMapping("/{id}/success-count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> incrementSuccessCount(@PathVariable String id) {
        freeTextQuestionService.incrementSuccessCount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Free text question success count incremented"));
    }

    @PostMapping("/{id}/fail-count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> incrementFailCount(@PathVariable String id) {
        freeTextQuestionService.incrementFailCount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Free text question fail count incremented"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFreeTextQuestion(@PathVariable String id, HttpServletRequest httpRequest) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        freeTextQuestionService.deleteFreeTextQuestion(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Free text question deleted"));
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteFreeTextQuestion(@PathVariable String id) {
        freeTextQuestionService.permanentlyDeleteFreeTextQuestion(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Free text question permanently deleted"));
    }
}
