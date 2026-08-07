package in.mapmytour.blog.staff.repository;

import in.mapmytour.blog.staff.entity.Staff;
import in.mapmytour.blog.staff.enums.Department;
import in.mapmytour.blog.staff.enums.StaffStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    Optional<Staff> findByIdNumber(String idNumber);

    Optional<Staff> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByIdNumber(String idNumber);

    List<Staff> findByDepartment(Department department);

    List<Staff> findByCity(String city);

    List<Staff> findByStatus(StaffStatus status);

    List<Staff> findByStateCode(String stateCode);

    List<Staff> findByStateCodeAndRtoCode(String stateCode, String rtoCode);

    List<Staff> findByStatusIn(List<StaffStatus> statuses);

    @Query("SELECT s FROM Staff s WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.idNumber) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.designation) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.city) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Staff> searchStaff(@Param("query") String query);

    @Query("SELECT COUNT(s) FROM Staff s WHERE s.stateCode = :stateCode AND s.rtoCode = :rtoCode " +
            "AND FUNCTION('TO_CHAR', s.issueDate, 'YYYY') = :year")
    long countByStateCodeAndRtoCodeAndIssueDateYear(@Param("stateCode") String stateCode,
                                                     @Param("rtoCode") String rtoCode,
                                                     @Param("year") String year);

    @Query("SELECT COUNT(s) FROM Staff s WHERE s.stateCode = :stateCode AND s.rtoCode = :rtoCode " +
            "AND EXTRACT(YEAR FROM s.issueDate) = :year")
    long countByStateCodeAndRtoCodeAndYear(@Param("stateCode") String stateCode,
                                            @Param("rtoCode") String rtoCode,
                                            @Param("year") int year);

    @Query("SELECT MAX(s.sequenceNumber) FROM Staff s WHERE s.stateCode = :stateCode AND s.rtoCode = :rtoCode " +
            "AND EXTRACT(YEAR FROM s.issueDate) = :year")
    Optional<Long> findMaxSequenceByStateCodeAndRtoCodeAndYear(@Param("stateCode") String stateCode,
                                                                @Param("rtoCode") String rtoCode,
                                                                @Param("year") int year);

    @Query("SELECT s FROM Staff s WHERE s.status = 'ACTIVE' AND s.validTill <= :expiryDate")
    List<Staff> findExpiringSoon(@Param("expiryDate") LocalDate expiryDate);

    @Query("SELECT s FROM Staff s WHERE s.status = 'ACTIVE' AND s.validTill < CURRENT_DATE")
    List<Staff> findExpiredIds();
}
