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
public class StaffPressIdDTO {
    private String idNumber;
    private String fullName;
    private String designation;
    private Department department;
    private String photoUrl;
    private String signatureUrl;
    private String city;
    private String state;
    private String district;
    private LocalDate dateOfBirth;
    private LocalDate issueDate;
    private LocalDate validTill;
    private StaffStatus status;
    private String validityStatusText;
    private String qrCodeUrl;
    private String workEmailMasked;
    private String mobileMasked;
    private String bloodGroupMasked;
    private String reporterBatchId;
}
