package org.ganjp.api.edu.sentence;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.auth.security.JwtUtils;
import org.ganjp.api.common.model.ApiResponse;
import org.ganjp.api.common.model.PaginatedResponse;
import org.ganjp.api.common.util.CmsUtil;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/edu-sentences")
@RequiredArgsConstructor
public class SentenceController {

    private final SentenceService sentenceService;
    private final JwtUtils jwtUtils;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<SentenceResponse>>> searchSentences(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Sentence.Language lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) String difficultyLevel
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, sortDirection, sort);
        Page<SentenceResponse> sentences = sentenceService.searchSentences(
                name, lang, tags, channel, isActive, term, week, difficultyLevel, pageable);
        PaginatedResponse<SentenceResponse> response = PaginatedResponse.of(
                sentences.getContent(), sentences.getNumber(), sentences.getSize(), sentences.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(response, "Sentences found"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SentenceResponse>> getSentence(@PathVariable String id) {
        SentenceResponse response = sentenceService.getSentenceById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Sentence found"));
    }

    @GetMapping("/by-language/{lang}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<SentenceResponse>>> getSentencesByLanguage(
            @PathVariable Sentence.Language lang,
            @RequestParam(defaultValue = "All") String channel
    ) {
        List<SentenceResponse> response = sentenceService.getSentencesByLanguageAndChannel(lang, channel);
        return ResponseEntity.ok(ApiResponse.success(response, "Sentences found"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SentenceResponse>> createSentence(
            @Valid @ModelAttribute SentenceCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        SentenceResponse response = sentenceService.createSentence(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Sentence created"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SentenceResponse>> createSentenceJson(
            @Valid @RequestBody SentenceCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        SentenceResponse response = sentenceService.createSentence(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Sentence created"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SentenceResponse>> updateSentence(
            @PathVariable String id,
            @Valid @ModelAttribute SentenceUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        SentenceResponse response = sentenceService.updateSentence(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Sentence updated"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SentenceResponse>> updateSentenceJson(
            @PathVariable String id,
            @Valid @RequestBody SentenceUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        SentenceResponse response = sentenceService.updateSentence(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Sentence updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSentence(@PathVariable String id, HttpServletRequest httpRequest) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        sentenceService.deleteSentence(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Sentence deleted"));
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteSentence(@PathVariable String id) {
        sentenceService.permanentlyDeleteSentence(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Sentence permanently deleted"));
    }

    @GetMapping("/audios/{filename}")
    public ResponseEntity<?> viewAudio(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader
    ) throws IOException {
        java.io.File file = sentenceService.getAudioFile(filename);
        long contentLength = file.length();
        String contentType = CmsUtil.determineContentType(filename);

        if (rangeHeader == null) {
            InputStreamResource full = new InputStreamResource(new java.io.FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + CmsUtil.sanitizeFilename(filename) + "\"")
                    .contentLength(contentLength)
                    .body(full);
        }

        HttpRange httpRange = HttpRange.parseRanges(rangeHeader).get(0);
        long start = httpRange.getRangeStart(contentLength);
        long end = httpRange.getRangeEnd(contentLength);
        long rangeLength = end - start + 1;

        java.io.InputStream rangeStream = new java.io.InputStream() {
            private final java.io.RandomAccessFile raf;
            private long remaining = rangeLength;
            {
                this.raf = new java.io.RandomAccessFile(file, "r");
                this.raf.seek(start);
            }
            @Override
            public int read() throws IOException {
                if (remaining <= 0) return -1;
                int b = raf.read();
                if (b != -1) remaining--;
                return b;
            }
            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (remaining <= 0) return -1;
                int toRead = (int) Math.min(len, remaining);
                int r = raf.read(b, off, toRead);
                if (r > 0) remaining -= r;
                return r;
            }
            @Override
            public void close() throws IOException {
                raf.close();
            }
        };

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + CmsUtil.sanitizeFilename(filename) + "\"")
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + contentLength)
                .contentLength(rangeLength)
                .body(new InputStreamResource(rangeStream));
    }
}
