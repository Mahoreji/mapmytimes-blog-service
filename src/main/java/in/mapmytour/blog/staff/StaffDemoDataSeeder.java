package in.mapmytour.blog.staff;

import in.mapmytour.blog.staff.entity.Staff;
import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.enums.StaffStatus;
import in.mapmytour.blog.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.demo-data", name = "seed-enabled", havingValue = "true", matchIfMissing = false)
public class StaffDemoDataSeeder {

    private final StaffRepository staffRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoStaffIfEmpty() {
        log.info("StaffDemoDataSeeder: verifying 4 demo records exist (idempotent seed). Current count = {}",
                staffRepository.count());

        String id1 = "MP-28-PM-07-08-26-000001";
        String id2 = "MP-09-RS-15-03-26-000002";
        String id3 = "DL-04-VK-22-11-26-000003";
        String id4 = "MH-12-SP-03-06-26-000004";

        LocalDate today = LocalDate.now();
        LocalDate validTill = today.plusYears(1);
        LocalDateTime now = LocalDateTime.now();

        if (!staffRepository.existsByIdNumber(id1)) {
            Staff s1 = Staff.builder()
                    .id(UUID.randomUUID())
                    .idNumber(id1)
                    .stateCode("MP")
                    .rtoCode("28")
                    .fullName("Prakhar Mahore")
                    .firstName("Prakhar")
                    .lastName("Mahore")
                    .designation("Principal Correspondent & Investigations Editor")
                    .department(Department.CHIEF_EDITOR)
                    .workEmail("prakhar.mahore@mapmytimes.com")
                    .personalEmail("prakhar.mahore.personal@gmail.com")
                    .mobilePrivate("+91 98939 89395")
                    .workMobile("+91 80859 27274")
                    .emergencyContactName("Monika Mahore")
                    .emergencyNumber("+91 94244 40080")
                    .bloodGroup("O+ve")
                    .dateOfBirth(LocalDate.of(1995, 8, 7))
                    .address("123 Press Colony, Residency Area, Indore")
                    .city("Indore")
                    .district("Indore")
                    .state("Madhya Pradesh")
                    .pinCode("452001")
                    .issueDate(today.minusMonths(3))
                    .validTill(validTill)
                    .lastRenewedDate(today.minusMonths(3))
                    .status(StaffStatus.ACTIVE)
                    .aadhaarLast4("8858")
                    .panLast4("XZ83")
                    .reporterBatchId("MMT-REP-0001")
                    .reissueRequested(false)
                    .sequenceNumber(1L)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            staffRepository.save(s1);
            log.info("StaffDemoDataSeeder: inserted s1 = {}", id1);
        }

        if (!staffRepository.existsByIdNumber(id2)) {
            Staff s2 = Staff.builder()
                    .id(UUID.randomUUID())
                    .idNumber(id2)
                    .stateCode("MP")
                    .rtoCode("09")
                    .fullName("Rahul Sharma")
                    .firstName("Rahul")
                    .lastName("Sharma")
                    .designation("Senior Ground Reporter — City Beat")
                    .department(Department.GROUND_REPORTER)
                    .workEmail("rahul.sharma@mapmytimes.com")
                    .personalEmail("rahul.sharma1990@gmail.com")
                    .mobilePrivate("+91 98260 11223")
                    .workMobile("+91 80859 27275")
                    .emergencyContactName("Sunita Sharma")
                    .emergencyNumber("+91 98260 44556")
                    .bloodGroup("B+ve")
                    .dateOfBirth(LocalDate.of(1990, 3, 15))
                    .address("45 Vijay Nagar, Scheme 78, Indore")
                    .city("Indore")
                    .district("Indore")
                    .state("Madhya Pradesh")
                    .pinCode("452010")
                    .issueDate(today.minusDays(10))
                    .validTill(validTill)
                    .lastRenewedDate(today.minusDays(10))
                    .status(StaffStatus.ACTIVE)
                    .aadhaarLast4("1122")
                    .panLast4("AB22")
                    .reporterBatchId("MMT-REP-0017")
                    .reissueRequested(false)
                    .sequenceNumber(2L)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            staffRepository.save(s2);
            log.info("StaffDemoDataSeeder: inserted s2 = {}", id2);
        }

        if (!staffRepository.existsByIdNumber(id3)) {
            Staff s3 = Staff.builder()
                    .id(UUID.randomUUID())
                    .idNumber(id3)
                    .stateCode("DL")
                    .rtoCode("04")
                    .fullName("Vikram Kapoor")
                    .firstName("Vikram")
                    .lastName("Kapoor")
                    .designation("Principal Cameraman — Features")
                    .department(Department.CAMERAMAN)
                    .workEmail("vikram.kapoor@mapmytimes.com")
                    .personalEmail("vikram.kapoor.delhi@gmail.com")
                    .mobilePrivate("+91 98110 99887")
                    .workMobile("+91 80859 27276")
                    .emergencyContactName("Priya Kapoor")
                    .emergencyNumber("+91 98110 11223")
                    .bloodGroup("A+ve")
                    .dateOfBirth(LocalDate.of(1988, 11, 22))
                    .address("C-14, Green Park Extension, New Delhi")
                    .city("New Delhi")
                    .district("New Delhi")
                    .state("Delhi")
                    .pinCode("110016")
                    .issueDate(today.minusDays(85))
                    .validTill(today.minusDays(5))
                    .lastRenewedDate(today.minusYears(1).minusDays(80))
                    .status(StaffStatus.EXPIRED)
                    .aadhaarLast4("9988")
                    .panLast4("KP99")
                    .reporterBatchId("MMT-CAM-0008")
                    .reissueRequested(true)
                    .reissueReason("Camera equipment upgradation and ID renewal for national assignments.")
                    .reissueRequestedAt(now.minusDays(2))
                    .sequenceNumber(3L)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            staffRepository.save(s3);
            log.info("StaffDemoDataSeeder: inserted s3 = {}", id3);
        }

        if (!staffRepository.existsByIdNumber(id4)) {
            Staff s4 = Staff.builder()
                    .id(UUID.randomUUID())
                    .idNumber(id4)
                    .stateCode("MH")
                    .rtoCode("12")
                    .fullName("Sneha Patil")
                    .firstName("Sneha")
                    .lastName("Patil")
                    .designation("Features Journalist — Culture & Lifestyle")
                    .department(Department.COLUMNIST)
                    .workEmail("sneha.patil@mapmytimes.com")
                    .personalEmail("sneha.patil.mumbai@gmail.com")
                    .mobilePrivate("+91 98670 34512")
                    .workMobile("+91 80859 27277")
                    .emergencyContactName("Rohan Patil")
                    .emergencyNumber("+91 98670 78901")
                    .bloodGroup("AB+ve")
                    .dateOfBirth(LocalDate.of(1994, 6, 3))
                    .address("Flat 304, Shanti Nivas, Bandra West, Mumbai")
                    .city("Mumbai")
                    .district("Mumbai Suburban")
                    .state("Maharashtra")
                    .pinCode("400050")
                    .issueDate(today.minusDays(45))
                    .validTill(today.plusDays(40))
                    .lastRenewedDate(today.minusDays(45))
                    .status(StaffStatus.ACTIVE)
                    .aadhaarLast4("3451")
                    .panLast4("XP34")
                    .reporterBatchId("MMT-FEA-0023")
                    .reissueRequested(false)
                    .sequenceNumber(4L)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            staffRepository.save(s4);
            log.info("StaffDemoDataSeeder: inserted s4 = {}", id4);
        }

        log.info("StaffDemoDataSeeder: idempotent seed complete. Total staff now = {}.",
                staffRepository.count());
    }
}
