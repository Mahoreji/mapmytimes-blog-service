package in.mapmytour.blog.staff.service;

import in.mapmytour.blog.staff.dto.*;
import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.enums.StaffStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffService {

    StaffAdminCreateRequestDTO createStaff(StaffAdminCreateRequestDTO dto, UUID createdBy);

    StaffAdminUpdateRequestDTO updateStaff(UUID staffId, StaffAdminUpdateRequestDTO dto, UUID updatedBy);

    void softDeleteStaff(UUID staffId, UUID updatedBy);

    List<StaffListCardDTO> getAllPublicStaff();

    List<StaffListCardDTO> getStaffByDepartment(Department department);

    List<StaffListCardDTO> getStaffByStatus(StaffStatus status);

    List<StaffListCardDTO> searchStaff(String query);

    Optional<StaffPressIdDTO> getPublicStaffByIdNumber(String idNumber);

    StaffVerifyResponseDTO verifyStaffByIdNumber(String idNumber);

    Optional<StaffProfileForSelfDTO> getOwnProfileByUserId(UUID userId);

    Optional<StaffProfileForSelfDTO> getStaffById(UUID staffId);

    String uploadStaffPhoto(UUID staffId, MultipartFile file) throws IOException;

    String uploadStaffSignature(UUID staffId, MultipartFile file) throws IOException;

    String generateIdNumber(String stateCode, String rtoCode, String firstName, String lastName, LocalDate issueDate);

    String maskMobile(String mobile);

    String maskEmail(String email);

    String maskBloodGroup(String bloodGroup);

    boolean isValid(StaffStatus status, LocalDate validTill);

    String regenerateIdNumber(UUID staffId, UUID updatedBy);

    List<StaffProfileForSelfDTO> getAllAdminStaff();

    void requestReissue(UUID userId, String reason);
}
