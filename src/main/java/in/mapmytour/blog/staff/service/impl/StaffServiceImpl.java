package in.mapmytour.blog.staff.service.impl;

import in.mapmytour.blog.helper.S3Helper;
import in.mapmytour.blog.staff.dto.*;
import in.mapmytour.blog.staff.entity.Staff;
import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.enums.StaffStatus;
import in.mapmytour.blog.staff.repository.StaffRepository;
import in.mapmytour.blog.staff.service.StaffService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final S3Helper s3Helper;

    @Override
    @Transactional
    public StaffAdminCreateRequestDTO createStaff(StaffAdminCreateRequestDTO dto, UUID createdBy) {
        if (dto.getUserId() != null && staffRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalStateException("Staff profile already exists for this user");
        }

        LocalDate issueDate = dto.getIssueDate() != null ? dto.getIssueDate() : LocalDate.now();
        LocalDate validTill = dto.getValidTill() != null ? dto.getValidTill() : issueDate.plusYears(1);

        String firstName = StringUtils.hasText(dto.getFirstName()) ? dto.getFirstName() : extractFirstName(dto.getFullName());
        String lastName = StringUtils.hasText(dto.getLastName()) ? dto.getLastName() : extractLastName(dto.getFullName());

        String idNumber = generateIdNumber(
                dto.getStateCode().toUpperCase(),
                dto.getRtoCode(),
                firstName,
                lastName,
                issueDate
        );

        long seqNum = extractSequenceFromIdNumber(idNumber);

        Staff staff = Staff.builder()
                .userId(dto.getUserId())
                .idNumber(idNumber)
                .sequenceNumber(seqNum)
                .stateCode(dto.getStateCode().toUpperCase())
                .rtoCode(dto.getRtoCode())
                .fullName(dto.getFullName())
                .firstName(firstName)
                .lastName(lastName)
                .designation(dto.getDesignation())
                .department(dto.getDepartment())
                .personalEmail(dto.getPersonalEmail())
                .workEmail(dto.getWorkEmail())
                .mobilePrivate(dto.getMobilePrivate())
                .workMobile(dto.getWorkMobile())
                .emergencyContactName(dto.getEmergencyContactName())
                .emergencyNumber(dto.getEmergencyNumber())
                .bloodGroup(dto.getBloodGroup())
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .state(dto.getState())
                .pinCode(dto.getPinCode())
                .issueDate(issueDate)
                .validTill(validTill)
                .aadhaarLast4(dto.getAadhaarLast4())
                .panLast4(dto.getPanLast4())
                .status(dto.getStatus() != null ? dto.getStatus() : StaffStatus.PENDING_APPROVAL)
                .reporterBatchId(dto.getReporterBatchId())
                .notes(dto.getNotes())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        String verifyUrl = String.format("/our-team/%s/public", idNumber);
        staff.setQrCodeUrl(verifyUrl);

        Staff saved = staffRepository.save(staff);
        log.info("Staff created with ID: {} by user: {}", saved.getIdNumber(), createdBy);
        return dto;
    }

    @Override
    @Transactional
    public StaffAdminUpdateRequestDTO updateStaff(UUID staffId, StaffAdminUpdateRequestDTO dto, UUID updatedBy) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with ID: " + staffId));

        if (dto.getUserId() != null && !dto.getUserId().equals(staff.getUserId())) {
            if (staffRepository.existsByUserId(dto.getUserId())) {
                throw new IllegalStateException("Staff profile already exists for this user");
            }
            staff.setUserId(dto.getUserId());
        }

        boolean idRegenNeeded = false;
        if (dto.getStateCode() != null && !dto.getStateCode().equalsIgnoreCase(staff.getStateCode())) idRegenNeeded = true;
        if (dto.getRtoCode() != null && !dto.getRtoCode().equals(staff.getRtoCode())) idRegenNeeded = true;
        if (dto.getFirstName() != null && !dto.getFirstName().equalsIgnoreCase(staff.getFirstName())) idRegenNeeded = true;
        if (dto.getLastName() != null && !dto.getLastName().equalsIgnoreCase(staff.getLastName())) idRegenNeeded = true;

        if (dto.getStateCode() != null) staff.setStateCode(dto.getStateCode().toUpperCase());
        if (dto.getRtoCode() != null) staff.setRtoCode(dto.getRtoCode());
        if (dto.getFullName() != null) staff.setFullName(dto.getFullName());
        if (dto.getFirstName() != null) staff.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) staff.setLastName(dto.getLastName());
        if (dto.getDesignation() != null) staff.setDesignation(dto.getDesignation());
        if (dto.getDepartment() != null) staff.setDepartment(dto.getDepartment());
        if (dto.getPersonalEmail() != null) staff.setPersonalEmail(dto.getPersonalEmail());
        if (dto.getWorkEmail() != null) staff.setWorkEmail(dto.getWorkEmail());
        if (dto.getMobilePrivate() != null) staff.setMobilePrivate(dto.getMobilePrivate());
        if (dto.getWorkMobile() != null) staff.setWorkMobile(dto.getWorkMobile());
        if (dto.getEmergencyContactName() != null) staff.setEmergencyContactName(dto.getEmergencyContactName());
        if (dto.getEmergencyNumber() != null) staff.setEmergencyNumber(dto.getEmergencyNumber());
        if (dto.getBloodGroup() != null) staff.setBloodGroup(dto.getBloodGroup());
        if (dto.getDateOfBirth() != null) staff.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getAddress() != null) staff.setAddress(dto.getAddress());
        if (dto.getCity() != null) staff.setCity(dto.getCity());
        if (dto.getDistrict() != null) staff.setDistrict(dto.getDistrict());
        if (dto.getState() != null) staff.setState(dto.getState());
        if (dto.getPinCode() != null) staff.setPinCode(dto.getPinCode());
        if (dto.getIssueDate() != null) staff.setIssueDate(dto.getIssueDate());
        if (dto.getValidTill() != null) staff.setValidTill(dto.getValidTill());
        if (dto.getLastRenewedDate() != null) staff.setLastRenewedDate(dto.getLastRenewedDate());
        if (dto.getAadhaarLast4() != null) staff.setAadhaarLast4(dto.getAadhaarLast4());
        if (dto.getPanLast4() != null) staff.setPanLast4(dto.getPanLast4());
        if (dto.getStatus() != null) staff.setStatus(dto.getStatus());
        if (dto.getReporterBatchId() != null) staff.setReporterBatchId(dto.getReporterBatchId());
        if (dto.getNotes() != null) staff.setNotes(dto.getNotes());
        staff.setUpdatedBy(updatedBy);

        staffRepository.save(staff);
        log.info("Staff updated: {} by user: {}", staffId, updatedBy);
        return dto;
    }

    @Override
    @Transactional
    public void softDeleteStaff(UUID staffId, UUID updatedBy) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with ID: " + staffId));
        staff.setStatus(StaffStatus.REVOKED);
        staff.setUpdatedBy(updatedBy);
        staffRepository.save(staff);
        log.info("Staff soft-deleted (REVOKED): {} by user: {}", staffId, updatedBy);
    }

    @Override
    public List<StaffListCardDTO> getAllPublicStaff() {
        return staffRepository.findByStatusIn(
                List.of(StaffStatus.ACTIVE, StaffStatus.UNDER_REVIEW)
        ).stream().map(this::toListCardDTO).collect(Collectors.toList());
    }

    @Override
    public List<StaffListCardDTO> getStaffByDepartment(Department department) {
        return staffRepository.findByDepartment(department).stream()
                .filter(s -> s.getStatus() == StaffStatus.ACTIVE)
                .map(this::toListCardDTO).collect(Collectors.toList());
    }

    @Override
    public List<StaffListCardDTO> getStaffByStatus(StaffStatus status) {
        return staffRepository.findByStatus(status).stream()
                .map(this::toListCardDTO).collect(Collectors.toList());
    }

    @Override
    public List<StaffListCardDTO> searchStaff(String query) {
        return staffRepository.searchStaff(query).stream()
                .filter(s -> s.getStatus() == StaffStatus.ACTIVE)
                .map(this::toListCardDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<StaffPressIdDTO> getPublicStaffByIdNumber(String idNumber) {
        return staffRepository.findByIdNumber(idNumber)
                .filter(s -> s.getStatus() != StaffStatus.REVOKED)
                .map(this::toPressIdDTO);
    }

    @Override
    public StaffVerifyResponseDTO verifyStaffByIdNumber(String idNumber) {
        Optional<Staff> staffOpt = staffRepository.findByIdNumber(idNumber);

        if (staffOpt.isEmpty()) {
            return StaffVerifyResponseDTO.builder()
                    .isValid(false)
                    .verificationMessage("ID Card not found in our records")
                    .idNumber(idNumber)
                    .verifyTimestamp(LocalDateTime.now())
                    .build();
        }

        Staff staff = staffOpt.get();
        boolean valid = isValid(staff.getStatus(), staff.getValidTill());
        String message;
        if (!valid) {
            if (staff.getStatus() == StaffStatus.REVOKED) {
                message = "This ID Card has been REVOKED";
            } else if (staff.getStatus() == StaffStatus.SUSPENDED) {
                message = "This ID Card is currently SUSPENDED";
            } else if (staff.getValidTill() != null && staff.getValidTill().isBefore(LocalDate.now())) {
                message = "This ID Card has EXPIRED. Renewal required.";
            } else if (staff.getStatus() == StaffStatus.PENDING_APPROVAL) {
                message = "This ID Card is pending approval";
            } else {
                message = "This ID Card is not currently valid";
            }
        } else {
            message = "VERIFIED - This is a valid MapMyTimes Press ID";
        }

        return StaffVerifyResponseDTO.builder()
                .isValid(valid)
                .verificationMessage(message)
                .fullName(staff.getFullName())
                .idNumber(staff.getIdNumber())
                .designation(staff.getDesignation())
                .department(staff.getDepartment())
                .photoUrl(staff.getPhotoUrl())
                .status(staff.getStatus())
                .validTill(staff.getValidTill())
                .city(staff.getCity())
                .state(staff.getState())
                .qrCodeUrl(staff.getQrCodeUrl())
                .verifyTimestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public Optional<StaffProfileForSelfDTO> getOwnProfileByUserId(UUID userId) {
        return staffRepository.findByUserId(userId).map(this::toSelfProfileDTO);
    }

    @Override
    public Optional<StaffProfileForSelfDTO> getStaffById(UUID staffId) {
        return staffRepository.findById(staffId).map(this::toSelfProfileDTO);
    }

    @Override
    @Transactional
    public String uploadStaffPhoto(UUID staffId, MultipartFile file) throws IOException {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with ID: " + staffId));
        String folder = "staff/" + staff.getId() + "/photo";
        String url = s3Helper.uploadImage(file, folder);
        staff.setPhotoUrl(url);
        staffRepository.save(staff);
        log.info("Staff photo uploaded: {}", staffId);
        return url;
    }

    @Override
    @Transactional
    public String uploadStaffSignature(UUID staffId, MultipartFile file) throws IOException {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with ID: " + staffId));
        String folder = "staff/" + staff.getId() + "/signature";
        String url = s3Helper.uploadImage(file, folder);
        staff.setSignatureUrl(url);
        staffRepository.save(staff);
        log.info("Staff signature uploaded: {}", staffId);
        return url;
    }

    @Override
    public String generateIdNumber(String stateCode, String rtoCode, String firstName, String lastName, LocalDate issueDate) {
        String initials = getInitials(firstName, lastName);
        String dd = String.format("%02d", issueDate.getDayOfMonth());
        String mm = String.format("%02d", issueDate.getMonthValue());
        String yy = String.format("%02d", issueDate.getYear() % 100);
        int year = issueDate.getYear();

        long nextSeq = 1L;
        Optional<Long> maxSeq = staffRepository.findMaxSequenceByStateCodeAndRtoCodeAndYear(stateCode, rtoCode, year);
        if (maxSeq.isPresent() && maxSeq.get() != null) {
            nextSeq = maxSeq.get() + 1L;
        }

        String seq = String.format("%06d", nextSeq);

        return String.format("%s-%s-%s-%s-%s-%s-%s",
                stateCode.toUpperCase(),
                rtoCode,
                initials.toUpperCase(),
                dd,
                mm,
                yy,
                seq);
    }

    @Override
    public String maskMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) return "";
        String digits = mobile.replaceAll("\\D", "");
        if (digits.length() < 7) return mobile;
        return digits.substring(0, 5) + "\u2022\u2022\u2022\u2022" + digits.substring(digits.length() - 2);
    }

    @Override
    public String maskEmail(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) return "";
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) return parts[0].charAt(0) + "\u2022\u2022@" + parts[1];
        return parts[0].charAt(0) + "\u2022\u2022\u2022" + parts[0].charAt(parts[0].length() - 1) + "@" + parts[1];
    }

    @Override
    public String maskBloodGroup(String bloodGroup) {
        if (!StringUtils.hasText(bloodGroup)) return "";
        return bloodGroup.replaceAll("[^ABO+-]", "");
    }

    @Override
    public boolean isValid(StaffStatus status, LocalDate validTill) {
        if (status != StaffStatus.ACTIVE) return false;
        if (validTill == null) return true;
        return !validTill.isBefore(LocalDate.now());
    }

    @Override
    @Transactional
    public String regenerateIdNumber(UUID staffId, UUID updatedBy) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with ID: " + staffId));
        LocalDate issueDate = staff.getIssueDate() != null ? staff.getIssueDate() : LocalDate.now();
        String newId = generateIdNumber(
                staff.getStateCode(),
                staff.getRtoCode(),
                staff.getFirstName(),
                staff.getLastName(),
                issueDate
        );
        long seqNum = extractSequenceFromIdNumber(newId);
        staff.setIdNumber(newId);
        staff.setSequenceNumber(seqNum);
        staff.setQrCodeUrl(String.format("/our-team/%s/public", newId));
        staff.setUpdatedBy(updatedBy);
        staffRepository.save(staff);
        log.info("Staff ID regenerated: {} -> {}", staffId, newId);
        return newId;
    }

    @Override
    public List<StaffProfileForSelfDTO> getAllAdminStaff() {
        return staffRepository.findAll().stream()
                .map(this::toSelfProfileDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void requestReissue(UUID userId, String reason) {
        Staff staff = staffRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Staff profile not found for user: " + userId));
        staff.setReissueRequested(true);
        staff.setReissueReason(reason);
        staff.setReissueRequestedAt(LocalDateTime.now());
        staffRepository.save(staff);
        log.info("Reissue requested for user: {} reason: {}", userId, reason);
    }

    private String getInitials(String firstName, String lastName) {
        char f = (StringUtils.hasText(firstName) && !firstName.isEmpty())
                ? Character.toUpperCase(firstName.trim().charAt(0)) : 'X';
        char l = (StringUtils.hasText(lastName) && !lastName.isEmpty())
                ? Character.toUpperCase(lastName.trim().charAt(0)) : 'X';
        return "" + f + l;
    }

    private long extractSequenceFromIdNumber(String idNumber) {
        try {
            String[] parts = idNumber.split("-");
            if (parts.length >= 7) {
                return Long.parseLong(parts[6]);
            } else if (parts.length >= 6) {
                return Long.parseLong(parts[5]);
            }
        } catch (Exception e) {
            log.warn("Could not extract sequence from: {}", idNumber);
        }
        return 1L;
    }

    private String extractFirstName(String fullName) {
        if (!StringUtils.hasText(fullName)) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }

    private String extractLastName(String fullName) {
        if (!StringUtils.hasText(fullName)) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }

    private StaffListCardDTO toListCardDTO(Staff s) {
        return StaffListCardDTO.builder()
                .id(s.getId() != null ? s.getId().toString() : null)
                .idNumber(s.getIdNumber())
                .fullName(s.getFullName())
                .designation(s.getDesignation())
                .department(s.getDepartment())
                .photoUrl(s.getPhotoUrl())
                .city(s.getCity())
                .state(s.getState())
                .validTill(s.getValidTill())
                .status(s.getStatus())
                .qrCodeUrl(s.getQrCodeUrl())
                .build();
    }

    private StaffPressIdDTO toPressIdDTO(Staff s) {
        String validityText;
        if (!isValid(s.getStatus(), s.getValidTill())) {
            if (s.getStatus() == StaffStatus.REVOKED) validityText = "REVOKED";
            else if (s.getStatus() == StaffStatus.SUSPENDED) validityText = "SUSPENDED";
            else if (s.getValidTill() != null && s.getValidTill().isBefore(LocalDate.now())) validityText = "EXPIRED";
            else validityText = "INACTIVE";
        } else {
            validityText = "VALID & ACTIVE";
        }
        return StaffPressIdDTO.builder()
                .idNumber(s.getIdNumber())
                .fullName(s.getFullName())
                .designation(s.getDesignation())
                .department(s.getDepartment())
                .photoUrl(s.getPhotoUrl())
                .signatureUrl(s.getSignatureUrl())
                .city(s.getCity())
                .state(s.getState())
                .district(s.getDistrict())
                .dateOfBirth(s.getDateOfBirth())
                .issueDate(s.getIssueDate())
                .validTill(s.getValidTill())
                .status(s.getStatus())
                .validityStatusText(validityText)
                .qrCodeUrl(s.getQrCodeUrl())
                .workEmailMasked(maskEmail(s.getWorkEmail()))
                .mobileMasked(maskMobile(s.getWorkMobile() != null ? s.getWorkMobile() : s.getMobilePrivate()))
                .bloodGroupMasked(maskBloodGroup(s.getBloodGroup()))
                .reporterBatchId(s.getReporterBatchId())
                .build();
    }

    private StaffProfileForSelfDTO toSelfProfileDTO(Staff s) {
        LocalDate today = LocalDate.now();
        long daysLeft = s.getValidTill() != null
                ? ChronoUnit.DAYS.between(today, s.getValidTill())
                : 9999L;
        LocalDate nextRenewal = s.getValidTill() != null
                ? s.getValidTill().minusDays(30)
                : today.plusDays(335);

        return StaffProfileForSelfDTO.builder()
                .staffId(s.getId())
                .userId(s.getUserId())
                .idNumber(s.getIdNumber())
                .fullName(s.getFullName())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .designation(s.getDesignation())
                .department(s.getDepartment())
                .photoUrl(s.getPhotoUrl())
                .signatureUrl(s.getSignatureUrl())
                .qrCodeUrl(s.getQrCodeUrl())
                .personalEmail(s.getPersonalEmail())
                .workEmail(s.getWorkEmail())
                .mobilePrivate(s.getMobilePrivate())
                .workMobile(s.getWorkMobile())
                .bloodGroup(s.getBloodGroup())
                .dateOfBirth(s.getDateOfBirth())
                .address(s.getAddress())
                .city(s.getCity())
                .district(s.getDistrict())
                .state(s.getState())
                .stateCode(s.getStateCode())
                .rtoCode(s.getRtoCode())
                .pinCode(s.getPinCode())
                .issueDate(s.getIssueDate())
                .validTill(s.getValidTill())
                .lastRenewedDate(s.getLastRenewedDate())
                .nextRenewalDate(nextRenewal)
                .daysUntilExpiry(daysLeft)
                .status(s.getStatus())
                .reporterBatchId(s.getReporterBatchId())
                .aadhaarLast4(s.getAadhaarLast4())
                .panLast4(s.getPanLast4())
                .emergencyContactName(s.getEmergencyContactName())
                .emergencyNumber(s.getEmergencyNumber())
                .reissueRequested(s.getReissueRequested())
                .reissueReason(s.getReissueReason())
                .notes(s.getNotes())
                .downloadUrl("/api/v1/admin/staff/" + s.getId() + "/download/pdf")
                .printUrl("/our-team/" + s.getIdNumber())
                .build();
    }
}
