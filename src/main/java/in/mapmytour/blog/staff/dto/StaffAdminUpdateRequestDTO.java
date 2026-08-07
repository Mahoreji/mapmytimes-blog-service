package in.mapmytour.blog.staff.dto;

import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.enums.StaffStatus;
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
public class StaffAdminUpdateRequestDTO {

    private UUID userId;

    @Size(max = 5)
    private String stateCode;

    @Size(max = 5)
    private String rtoCode;

    private String fullName;

    private String firstName;

    private String lastName;

    private String designation;

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

    private LocalDate issueDate;

    private LocalDate validTill;

    private LocalDate lastRenewedDate;

    private String aadhaarLast4;

    private String panLast4;

    private StaffStatus status;

    private String reporterBatchId;

    private String notes;
}
