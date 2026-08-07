package in.mapmytour.blog.staff.controller;

import in.mapmytour.blog.dto.APIResponse;
import in.mapmytour.blog.staff.dto.StaffListCardDTO;
import in.mapmytour.blog.staff.dto.StaffPressIdDTO;
import in.mapmytour.blog.staff.dto.StaffVerifyResponseDTO;
import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
@Slf4j
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<APIResponse<List<StaffListCardDTO>>> getAllPublicStaff() {
        log.debug("GET /api/v1/staff - public list request");
        List<StaffListCardDTO> list = staffService.getAllPublicStaff();
        return ResponseEntity.ok(
                APIResponse.<List<StaffListCardDTO>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Active staff list retrieved successfully")
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<APIResponse<List<StaffListCardDTO>>> getByDepartment(
            @PathVariable Department department) {
        log.debug("GET /api/v1/staff/department/{}", department);
        List<StaffListCardDTO> list = staffService.getStaffByDepartment(department);
        return ResponseEntity.ok(
                APIResponse.<List<StaffListCardDTO>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Staff list for department: " + department)
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse<List<StaffListCardDTO>>> searchStaff(
            @RequestParam("q") String query) {
        log.debug("GET /api/v1/staff/search?q={}", query);
        List<StaffListCardDTO> list = staffService.searchStaff(query);
        return ResponseEntity.ok(
                APIResponse.<List<StaffListCardDTO>>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Search results for: " + query)
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/{idNumber}")
    public ResponseEntity<APIResponse<StaffPressIdDTO>> getPublicStaffDetail(
            @PathVariable String idNumber) {
        log.debug("GET /api/v1/staff/{} - public detail", idNumber);
        Optional<StaffPressIdDTO> dto = staffService.getPublicStaffByIdNumber(idNumber);
        if (dto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.<StaffPressIdDTO>builder()
                            .success(false)
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Staff ID not found or has been revoked")
                            .build());
        }
        return ResponseEntity.ok(
                APIResponse.<StaffPressIdDTO>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Press ID card retrieved")
                        .data(dto.get())
                        .build()
        );
    }

    @GetMapping("/{idNumber}/public")
    public ResponseEntity<APIResponse<StaffPressIdDTO>> getPublicStaffAlias(
            @PathVariable String idNumber) {
        return getPublicStaffDetail(idNumber);
    }

    @GetMapping("/verify/{idNumber}")
    public ResponseEntity<APIResponse<StaffVerifyResponseDTO>> verifyStaff(
            @PathVariable String idNumber) {
        log.info("GET /api/v1/staff/verify/{} - verification request", idNumber);
        StaffVerifyResponseDTO result = staffService.verifyStaffByIdNumber(idNumber);
        return ResponseEntity.ok()
                .body(APIResponse.<StaffVerifyResponseDTO>builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message(result.getVerificationMessage())
                        .data(result)
                        .build());
    }
}
