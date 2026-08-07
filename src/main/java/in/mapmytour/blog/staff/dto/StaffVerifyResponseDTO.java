package in.mapmytour.blog.staff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.enums.StaffStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffVerifyResponseDTO {
    @JsonProperty("isValid")
    private boolean isValid;
    private String verificationMessage;
    private String fullName;
    private String idNumber;
    private String designation;
    private Department department;
    private String photoUrl;
    private StaffStatus status;
    private LocalDate validTill;
    private String city;
    private String state;
    private String qrCodeUrl;
    private LocalDateTime verifyTimestamp;
}
