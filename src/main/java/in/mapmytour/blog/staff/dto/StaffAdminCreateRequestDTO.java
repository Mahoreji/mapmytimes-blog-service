package in.mapmytour.blog.staff.dto;

import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.enums.StaffStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffAdminCreateRequestDTO {

    private UUID userId;

    @NotBlank(message = "State code is required (e.g., MP, DL)")
    @Size(max = 5)
    private String stateCode;

    @NotBlank(message = "RTO code is required (e.g., 28, 09)")
    @Size(max = 5)
    private String rtoCode;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String firstName;

    private String lastName;

    private String designation;

    @NotNull(message = "Department is required")
    private Department department;

    private String personalEmail;

    private String workEmail;

    private String mobilePrivate;

    private String workMobile;

    private String emergencyContactName;

    private String emergencyNumber;

    private String bloodGroup;

    private LocalDate dateOfBirth;

    private String address;

    private String city;

    private String district;

    private String state;

    private String pinCode;

    @Builder.Default
    private LocalDate issueDate = LocalDate.now();

    private LocalDate validTill;

    private String aadhaarLast4;

    private String panLast4;

    @Builder.Default
    private StaffStatus status = StaffStatus.PENDING_APPROVAL;

    private String reporterBatchId;

    private String notes;
}
