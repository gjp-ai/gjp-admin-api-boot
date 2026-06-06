package org.ganjp.api.edu.questionimage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.auth.security.JwtUtils;
import org.ganjp.api.common.model.ApiResponse;
import org.ganjp.api.common.util.CmsUtil;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/edu-question-images")
@RequiredArgsConstructor
public class QuestionImageController {

    private final QuestionImageService questionImageService;
    private final JwtUtils jwtUtils;

    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<InputStreamResource> viewImage(@PathVariable String filename) throws IOException {
        java.io.File file = questionImageService.getImageFile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(CmsUtil.determineContentType(filename)))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + CmsUtil.sanitizeFilename(filename) + "\"")
                .contentLength(file.length())
                .body(new InputStreamResource(new FileInputStream(file)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionImageResponse>>> searchQuestionImages(
            @RequestParam(required = false) String multipleChoiceQuestionId,
            @RequestParam(required = false) String freeTextQuestionId,
            @RequestParam(required = false) String trueFalseQuestionId,
            @RequestParam(required = false) String fillBlankQuestionId,
            @RequestParam(required = false) QuestionImage.Language lang,
            @RequestParam(required = false) Boolean isActive
    ) {
        List<QuestionImageResponse> images = questionImageService.searchQuestionImages(
                multipleChoiceQuestionId, freeTextQuestionId, trueFalseQuestionId, fillBlankQuestionId, lang, isActive);
        return ResponseEntity.ok(ApiResponse.success(images, "Question images found"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<QuestionImageResponse>> getQuestionImage(@PathVariable String id) {
        QuestionImageResponse response = questionImageService.getQuestionImageById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Question image found"));
    }

    @GetMapping("/multiple-choice-question/{multipleChoiceQuestionId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionImageResponse>>> getImagesByMultipleChoiceQuestion(@PathVariable String multipleChoiceQuestionId) {
        return ResponseEntity.ok(ApiResponse.success(
                questionImageService.listByMultipleChoiceQuestion(multipleChoiceQuestionId),
                "Question images found"));
    }

    @GetMapping("/free-text-question/{freeTextQuestionId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionImageResponse>>> getImagesByFreeTextQuestion(@PathVariable String freeTextQuestionId) {
        return ResponseEntity.ok(ApiResponse.success(
                questionImageService.listByFreeTextQuestion(freeTextQuestionId),
                "Question images found"));
    }

    @GetMapping("/true-false-question/{trueFalseQuestionId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionImageResponse>>> getImagesByTrueFalseQuestion(@PathVariable String trueFalseQuestionId) {
        return ResponseEntity.ok(ApiResponse.success(
                questionImageService.listByTrueFalseQuestion(trueFalseQuestionId),
                "Question images found"));
    }

    @GetMapping("/fill-blank-question/{fillBlankQuestionId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionImageResponse>>> getImagesByFillBlankQuestion(@PathVariable String fillBlankQuestionId) {
        return ResponseEntity.ok(ApiResponse.success(
                questionImageService.listByFillBlankQuestion(fillBlankQuestionId),
                "Question images found"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<QuestionImageResponse>> createQuestionImageJson(
            @Valid @RequestBody QuestionImageCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        QuestionImageResponse response = questionImageService.createQuestionImage(request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Question image created"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<QuestionImageResponse>> createQuestionImage(
            @Valid @ModelAttribute QuestionImageCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        QuestionImageResponse response = questionImageService.createQuestionImage(request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Question image created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<QuestionImageResponse>> updateQuestionImage(
            @PathVariable String id,
            @Valid @RequestBody QuestionImageUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        QuestionImageResponse response = questionImageService.updateQuestionImage(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Question image updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionImage(@PathVariable String id, HttpServletRequest httpRequest) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        questionImageService.deleteQuestionImage(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Question image deleted"));
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteQuestionImage(@PathVariable String id) {
        questionImageService.permanentlyDeleteQuestionImage(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Question image permanently deleted"));
    }
}
