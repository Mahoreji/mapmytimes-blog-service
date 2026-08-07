package in.mapmytour.blog.staff.service.impl;

import in.mapmytour.blog.staff.dto.StaffPressIdDTO;
import in.mapmytour.blog.staff.dto.StaffProfileForSelfDTO;
import in.mapmytour.blog.staff.entity.Staff;
import in.mapmytour.blog.staff.repository.StaffRepository;
import in.mapmytour.blog.staff.service.StaffIdCardService;
import in.mapmytour.blog.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffIdCardServiceImpl implements StaffIdCardService {

    private final StaffRepository staffRepository;
    private final StaffService staffService;

    @Override
    public Optional<StaffPressIdDTO> getIdCardDataByIdNumber(String idNumber) {
        return staffService.getPublicStaffByIdNumber(idNumber);
    }

    @Override
    public Optional<StaffProfileForSelfDTO> getIdCardDataForSelf(UUID userId) {
        return staffService.getOwnProfileByUserId(userId);
    }

    @Override
    public Map<String, Object> getIdCardTemplateModelByIdNumber(String idNumber) {
        Map<String, Object> model = new HashMap<>();
        Optional<Staff> staffOpt = staffRepository.findByIdNumber(idNumber);
        if (staffOpt.isPresent()) {
            Staff s = staffOpt.get();
            Optional<StaffPressIdDTO> dtoOpt = staffService.getPublicStaffByIdNumber(idNumber);
            dtoOpt.ifPresent(dto -> model.put("card", dto));
            model.put("staffId", s.getId());
        }
        model.put("cardSize", "CR80");
        model.put("cardWidthMm", 85.6);
        model.put("cardHeightMm", 53.98);
        model.put("printUrl", getPrintPageUrl(idNumber));
        return model;
    }

    @Override
    public byte[] generateIdCardPdf(UUID staffId) {
        log.warn("PDF generation requested for staff: {} - server-side PDF generation not implemented yet. " +
                "Please use client-side jsPDF + html2canvas in frontend.", staffId);
        throw new UnsupportedOperationException("Server-side PDF generation will be available after HTML template integration. " +
                "Please use client-side download in the dashboard (jsPDF + html2canvas).");
    }

    @Override
    public byte[] generateIdCardPng(UUID staffId) {
        log.warn("PNG generation requested for staff: {} - server-side PNG render not implemented yet.", staffId);
        throw new UnsupportedOperationException("Server-side PNG rendering not yet available. Use browser screenshot/print.");
    }

    @Override
    public String getPrintPageUrl(String idNumber) {
        return "/our-team/" + idNumber;
    }

    @Override
    public String getDownloadPdfUrl(UUID staffId) {
        return "/api/v1/admin/staff/" + staffId + "/download/pdf";
    }

    @Override
    public String getIdCardHtmlTemplate() {
        return "<p>HTML template will be integrated from your reference file.</p>";
    }
}
