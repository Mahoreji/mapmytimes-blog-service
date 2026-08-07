package in.mapmytour.blog.staff.controller;

import in.mapmytour.blog.dto.APIResponse;
import in.mapmytour.blog.service.UserContextService;
import in.mapmytour.blog.staff.dto.StaffAdminCreateRequestDTO;
import in.mapmytour.blog.staff.dto.StaffAdminUpdateRequestDTO;
import in.mapmytour.blog.staff.dto.StaffProfileForSelfDTO;
import in.mapmytour.blog.staff.service.StaffIdCardService;
import in.mapmytour.blog.staff.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/staff")
@RequiredArgsConstructor
@Slf4j
public class StaffAdminController {

    private final StaffService staffService;
    private final StaffIdCardService staffIdCardService;
    private final UserContextService userContextService;

    private UUID getUUIDFromIdStr(String idStr) {
        try {
            return UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid staff ID format: " + idStr);
        }
    }

    private UUID getCurrentUserId(HttpServletRequest request) {
        String userIdStr = userContextService.getCurrentUserId(request);
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            log.warn("No userId found in request headers for admin staff operation");
            return null;
        }
        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid userId header format: {}", userIdStr);
            return null;
        }
    }

    private boolean isStaffAdmin(HttpServletRequest request) {
        String role = userContextService.getCurrentUserRole(request);
        return "ADMIN".equalsIgnoreCase(role) ||
                "SUPER_ADMIN".equalsIgnoreCase(role) ||
                "STAFF_ADMIN".equalsIgnoreCase(role);
    }

    @GetMapping("/me")
    public ResponseEntity<APIResponse<StaffProfileForSelfDTO>> getMyProfile(HttpServletRequest request) {
        UUID userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.<StaffProfileForSelfDTO>builder()
                            .success(false)
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .message("Authentication required - user not identified")
                            .build());
        }

        String role = userContextService.getCurrentUserRole(request);
        log.info("GET /api/v1/admin/staff/me - user: {} role: {}", userId, role);

        Optional<StaffProfileForSelfDTO> profile = staffService.getOwnProfileByUserId(userId);
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.<StaffProfileForSelfDTO>builder()
                            .success(false)
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("No Press ID profile found for your account. Please contact HR/Staff Admin.")
                            .build());
        }
        return ResponseEntity.ok(
                APIResponse.<StaffProfileForSelfDTO>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Your Press ID profile retrieved")
                        .data(profile.get())
                        .build()
        );
    }

    @PostMapping("/me/reissue")
    public ResponseEntity<APIResponse<Void>> requestReissue(
            @RequestBody(required = false) ReissueRequest reissueReq,
            HttpServletRequest request) {
        UUID userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.<Void>builder()
                            .success(false)
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .message("Authentication required")
                            .build());
        }
        String reason = reissueReq != null ? reissueReq.getReason() : "Requested via dashboard";
        staffService.requestReissue(userId, reason);
        return ResponseEntity.ok(
                APIResponse.<Void>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Re-issue request submitted successfully. Staff Admin will review shortly.")
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<StaffProfileForSelfDTO>>> getAllStaff(HttpServletRequest request) {
        if (!isStaffAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(APIResponse.<List<StaffProfileForSelfDTO>>builder()
                            .success(false)
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .message("STAFF_ADMIN, ADMIN or SUPER_ADMIN role required")
                            .build());
        }
        UUID adminId = getCurrentUserId(request);
        log.info("GET /api/v1/admin/staff - list all by admin: {}", adminId);
        List<StaffProfileForSelfDTO> list = staffService.getAllAdminStaff();
        return ResponseEntity.ok(
                APIResponse.<List<StaffProfileForSelfDTO>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Full staff list retrieved")
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<StaffProfileForSelfDTO>> getStaffById(
            @PathVariable String id,
            HttpServletRequest request) {
        if (!isStaffAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(forbiddenResp("STAFF_ADMIN role required"));
        }
        UUID staffId = getUUIDFromIdStr(id);
        Optional<StaffProfileForSelfDTO> staff = staffService.getStaffById(staffId);
        if (staff.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(notFoundResp("Staff not found"));
        }
        return ResponseEntity.ok(
                APIResponse.<StaffProfileForSelfDTO>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Staff profile retrieved")
                        .data(staff.get())
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<APIResponse<StaffAdminCreateRequestDTO>> createStaff(
            @Valid @RequestBody StaffAdminCreateRequestDTO dto,
            HttpServletRequest request) {
        if (!isStaffAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(forbiddenRespCreate("Only STAFF_ADMIN / ADMIN / SUPER_ADMIN can create staff profiles"));
        }
        UUID createdBy = getCurrentUserId(request);
        log.info("POST /api/v1/admin/staff - create by admin: {} for user {}",
                createdBy, dto.getUserId());
        try {
            StaffAdminCreateRequestDTO created = staffService.createStaff(dto, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(APIResponse.<StaffAdminCreateRequestDTO>builder()
                            .success(true)
                            .statusCode(HttpStatus.CREATED.value())
                            .message("Staff profile created. ID number will be auto-generated.")
                            .data(created)
                            .build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(APIResponse.<StaffAdminCreateRequestDTO>builder()
                            .success(false)
                            .statusCode(HttpStatus.CONFLICT.value())
                            .message(e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<StaffAdminUpdateRequestDTO>> updateStaff(
            @PathVariable String id,
            @Valid @RequestBody StaffAdminUpdateRequestDTO dto,
            HttpServletRequest request) {
        if (!isStaffAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(forbiddenRespUpdate("STAFF_ADMIN role required"));
        }
        UUID updatedBy = getCurrentUserId(request);
        UUID staffId = getUUIDFromIdStr(id);
        log.info("PUT /api/v1/admin/staff/{} - update by admin: {}", id, updatedBy);
        try {
            StaffAdminUpdateRequestDTO updated = staffService.updateStaff(staffId, dto, updatedBy);
            return ResponseEntity.ok(
                    APIResponse.<StaffAdminUpdateRequestDTO>builder()
                            .success(true)
                            .statusCode(HttpStatus.OK.value())
                            .message("Staff profile updated")
                            .data(updated)
                            .build()
            );
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(notFoundRespUpdate(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> revokeStaff(
            @PathVariable String id,
            HttpServletRequest request) {
        if (!isStaffAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(forbiddenRespVoid("STAFF_ADMIN role required to revoke"));
        }
        UUID updatedBy = getCurrentUserId(request);
        UUID staffId = getUUIDFromIdStr(id);
        log.warn("DELETE /api/v1/admin/staff/{} - REVOKE by admin: {}", id, updatedBy);
        staffService.softDeleteStaff(staffId, updatedBy);
        return ResponseEntity.ok(
                APIResponse.<Void>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Staff ID revoked (soft delete - status set to REVOKED)")
                        .build()
        );
    }

    @PostMapping(path = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<String>> uploadPhoto(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        if (!isStaffAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(forbiddenRespString("STAFF_ADMIN role required for photo upload"));
        }
        UUID staffId = getUUIDFromIdStr(id);
        UUID uploader = getCurrentUserId(request);
        log.info("POST /api/v1/admin/staff/{}/photo - upload by: {}", id, uploader);
        try {
            String url = staffService.uploadStaffPhoto(staffId, file);
            return ResponseEntity.ok(
                    APIResponse.<String>builder()
                            .success(true)
                            .statusCode(HttpStatus.OK.value())
                            .message("Photo uploaded successfully")
                            .data(url)
                            .build()
            );
        } catch (IOException e) {
            log.error("Photo upload failed for staff {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.<String>builder()
                            .success(false)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Photo upload failed: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping(path = "/{id}/signature", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<String>> uploadSignature(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        if (!isStaffAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(forbiddenRespString("STAFF_ADMIN role required for signature upload"));
        }
        UUID staffId = getUUIDFromIdStr(id);
        UUID uploader = getCurrentUserId(request);
        log.info("POST /api/v1/admin/staff/{}/signature - upload by: {}", id, uploader);
        try {
            String url = staffService.uploadStaffSignature(staffId, file);
            return ResponseEntity.ok(
                    APIResponse.<String>builder()
                            .success(true)
                            .statusCode(HttpStatus.OK.value())
                            .message("Signature uploaded successfully")
                            .data(url)
                            .build()
            );
        } catch (IOException e) {
            log.error("Signature upload failed for staff {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.<String>builder()
                            .success(false)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Signature upload failed: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/{id}/regenerate-id")
    public ResponseEntity<APIResponse<String>> regenerateId(
            @PathVariable String id,
            HttpServletRequest request) {
        if (!isStaffAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(forbiddenRespString("STAFF_ADMIN role required to regenerate ID"));
        }
        UUID updatedBy = getCurrentUserId(request);
        UUID staffId = getUUIDFromIdStr(id);
        log.info("POST /api/v1/admin/staff/{}/regenerate-id - by admin: {}", id, updatedBy);
        try {
            String newId = staffService.regenerateIdNumber(staffId, updatedBy);
            return ResponseEntity.ok(
                    APIResponse.<String>builder()
                            .success(true)
                            .statusCode(HttpStatus.OK.value())
                            .message("Staff ID number regenerated successfully")
                            .data(newId)
                            .build()
            );
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(notFoundRespString(e.getMessage()));
        }
    }

    @GetMapping("/{id}/download/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable String id,
            HttpServletRequest request) {
        UUID requesterId = getCurrentUserId(request);
        String role = userContextService.getCurrentUserRole(request);
        UUID staffId = getUUIDFromIdStr(id);

        Optional<StaffProfileForSelfDTO> staffOpt = staffService.getStaffById(staffId);
        if (staffOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        StaffProfileForSelfDTO s = staffOpt.get();
        boolean isOwner = s.getUserId() != null && s.getUserId().equals(requesterId);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role) || "SUPER_ADMIN".equalsIgnoreCase(role) ||
                "STAFF_ADMIN".equalsIgnoreCase(role);

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            byte[] pdf = staffIdCardService.generateIdCardPdf(staffId);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + s.getIdNumber() + ".pdf\"")
                    .header("Content-Type", "application/pdf")
                    .body(pdf);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .header("X-Reason", e.getMessage())
                    .build();
        }
    }

    @Data
    public static class ReissueRequest {
        private String reason;
    }

    private APIResponse<StaffProfileForSelfDTO> unauthResp() {
        return APIResponse.<StaffProfileForSelfDTO>builder()
                .success(false)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Authentication required")
                .build();
    }

    private APIResponse<StaffProfileForSelfDTO> forbiddenResp(String msg) {
        return APIResponse.<StaffProfileForSelfDTO>builder()
                .success(false)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(msg)
                .build();
    }

    private APIResponse<StaffProfileForSelfDTO> notFoundResp(String msg) {
        return APIResponse.<StaffProfileForSelfDTO>builder()
                .success(false)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(msg)
                .build();
    }

    private APIResponse<StaffAdminCreateRequestDTO> forbiddenRespCreate(String msg) {
        return APIResponse.<StaffAdminCreateRequestDTO>builder()
                .success(false)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(msg)
                .build();
    }

    private APIResponse<StaffAdminUpdateRequestDTO> forbiddenRespUpdate(String msg) {
        return APIResponse.<StaffAdminUpdateRequestDTO>builder()
                .success(false)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(msg)
                .build();
    }

    private APIResponse<StaffAdminUpdateRequestDTO> notFoundRespUpdate(String msg) {
        return APIResponse.<StaffAdminUpdateRequestDTO>builder()
                .success(false)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(msg)
                .build();
    }

    private APIResponse<Void> forbiddenRespVoid(String msg) {
        return APIResponse.<Void>builder()
                .success(false)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(msg)
                .build();
    }

    private APIResponse<String> forbiddenRespString(String msg) {
        return APIResponse.<String>builder()
                .success(false)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(msg)
                .build();
    }

    private APIResponse<String> notFoundRespString(String msg) {
        return APIResponse.<String>builder()
                .success(false)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(msg)
                .build();
    }
}
