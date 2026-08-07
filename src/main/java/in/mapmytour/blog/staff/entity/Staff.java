package in.mapmytour.blog.staff.entity;

import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.enums.StaffStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", unique = true)
    private UUID userId;

    @Column(name = "id_number", unique = true, nullable = false, length = 50)
    private String idNumber;

    @Column(name = "state_code", nullable = false, length = 5)
    private String stateCode;

    @Column(name = "rto_code", nullable = false, length = 5)
    private String rtoCode;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "first_name", length = 150)
    private String firstName;

    @Column(name = "last_name", length = 150)
    private String lastName;

    @Column(name = "designation", length = 255)
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(name = "department", nullable = false, length = 50)
    private Department department;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "signature_url", columnDefinition = "TEXT")
    private String signatureUrl;

    @Column(name = "qr_code_url", columnDefinition = "TEXT")
    private String qrCodeUrl;

    @Column(name = "personal_email", length = 255)
    private String personalEmail;

    @Column(name = "work_email", length = 255)
    private String workEmail;

    @Column(name = "mobile_private", length = 20)
    private String mobilePrivate;

    @Column(name = "work_mobile", length = 20)
    private String workMobile;

    @Column(name = "emergency_contact_name", length = 255)
    private String emergencyContactName;

    @Column(name = "emergency_number", length = 20)
    private String emergencyNumber;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 150)
    private String city;

    @Column(name = "district", length = 150)
    private String district;

    @Column(name = "state", length = 150)
    private String state;

    @Column(name = "pin_code", length = 20)
    private String pinCode;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "valid_till")
    private LocalDate validTill;

    @Column(name = "last_renewed_date")
    private LocalDate lastRenewedDate;

    @Column(name = "aadhaar_last4", length = 4)
    private String aadhaarLast4;

    @Column(name = "pan_last4", length = 4)
    private String panLast4;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private StaffStatus status = StaffStatus.PENDING_APPROVAL;

    @Column(name = "reporter_batch_id", length = 100)
    private String reporterBatchId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "reissue_requested")
    @Builder.Default
    private Boolean reissueRequested = false;

    @Column(name = "reissue_reason", columnDefinition = "TEXT")
    private String reissueReason;

    @Column(name = "reissue_requested_at")
    private LocalDateTime reissueRequestedAt;

    @Column(name = "sequence_number")
    private Long sequenceNumber;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = StaffStatus.PENDING_APPROVAL;
        }
        if (this.reissueRequested == null) {
            this.reissueRequested = false;
        }
    }
}
