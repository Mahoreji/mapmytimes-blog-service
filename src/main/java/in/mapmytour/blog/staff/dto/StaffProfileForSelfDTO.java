package in.mapmytour.blog.staff.dto;

import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.enums.StaffStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffProfileForSelfDTO {
    private java.util.UUID staffId;
    private java.util.UUID userId;
    private String idNumber;
    private String fullName;
    private String firstName;
    private String lastName;
    private String designation;
    private Department department;
    private String photoUrl;
    private String signatureUrl;
    private String qrCodeUrl;
    private String personalEmail;
    private String workEmail;
    private String mobilePrivate;
    private String workMobile;
    private String bloodGroup;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String district;
    private String state;
    private String stateCode;
    private String rtoCode;
    private String pinCode;
    private LocalDate issueDate;
    private LocalDate validTill;
    private LocalDate lastRenewedDate;
    private LocalDate nextRenewalDate;
    private long daysUntilExpiry;
    private StaffStatus status;
    private String reporterBatchId;
    private String aadhaarLast4;
    private String panLast4;
    private String emergencyContactName;
    private String emergencyNumber;
    private Boolean reissueRequested;
    private String reissueReason;
    private String notes;
    private String downloadUrl;
    private String printUrl;
}
