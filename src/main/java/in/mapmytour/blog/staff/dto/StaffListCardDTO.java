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
public class StaffListCardDTO {
    private String id;
    private String idNumber;
    private String fullName;
    private String designation;
    private Department department;
    private String photoUrl;
    private String city;
    private String state;
    private LocalDate validTill;
    private StaffStatus status;
    private String qrCodeUrl;
}
