package org.ganjp.api.cms.file;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.ganjp.api.auth.security.JwtUtils;
import org.ganjp.api.common.model.ApiResponse;
import org.ganjp.api.common.model.PaginatedResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    private final JwtUtils jwtUtils;

    /**
     * List files with pagination and filtering
     * GET /v1/files?name=xxx&lang=EN&tags=yyy&isActive=true&page=0&size=20&sort=updatedAt&direction=desc
     * 
     * @param page Page number (0-based)
     * @param size Page size
     * @param sort Sort field (e.g., updatedAt, createdAt, name)
     * @param direction Sort direction (asc or desc)
     * @param name Optional name filter
     * @param lang Optional language filter
     * @param tags Optional tags filter
     * @param isActive Optional active status filter
     * @return List of files
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<FileResponse>>> listFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) FileAsset.Language lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
            ? Sort.Direction.DESC : Sort.Direction.ASC;

        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, sortDirection, sort);

        Page<FileResponse> list = fileService.searchFiles(name, lang, tags, isActive, pageable);

        PaginatedResponse<FileResponse> response = PaginatedResponse.of(list.getContent(), list.getNumber(), list.getSize(), list.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(response, "Files found"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FileResponse>> createFile(@Valid @ModelAttribute FileCreateRequest request, HttpServletRequest httpRequest) throws IOException {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        FileResponse r = fileService.createFile(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(r, "File created"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FileResponse>> createFileJson(@Valid @RequestBody FileCreateRequest request, HttpServletRequest httpRequest) throws IOException {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        FileResponse r = fileService.createFile(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(r, "File created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FileResponse>> getFile(@PathVariable String id) {
        FileResponse r = fileService.getFileById(id);
        return ResponseEntity.ok(ApiResponse.success(r, "File found"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FileResponse>> updateFile(@PathVariable String id, @Valid @ModelAttribute FileUpdateRequest request, HttpServletRequest httpRequest) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        FileResponse r = fileService.updateFile(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success(r, "File updated"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FileResponse>> updateFileJson(@PathVariable String id, @Valid @RequestBody FileUpdateRequest request, HttpServletRequest httpRequest) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        FileResponse r = fileService.updateFile(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success(r, "File updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String id, HttpServletRequest httpRequest) {
        String userId = jwtUtils.extractUserIdFromToken(httpRequest);
        fileService.deleteFile(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "File deleted"));
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteFile(@PathVariable String id) {
        fileService.permanentlyDeleteFile(id);
        return ResponseEntity.ok(ApiResponse.success(null, "File permanently deleted"));
    }

    // download file by filename (secured)
    @GetMapping("/download/{filename}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> downloadByFilename(@PathVariable String filename) throws IOException {
        java.io.File file = fileService.getFileByFilename(filename);
        java.io.InputStream is = new java.io.FileInputStream(file);
        InputStreamResource resource = new InputStreamResource(is);
        String ct = org.ganjp.api.common.util.CmsUtil.determineContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ct))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + org.ganjp.api.common.util.CmsUtil.sanitizeFilename(filename) + "\"")
                .contentLength(file.length())
                .body(resource);
    }
}
