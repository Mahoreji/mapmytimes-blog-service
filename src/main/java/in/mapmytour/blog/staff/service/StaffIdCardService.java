package in.mapmytour.blog.staff.service;

import in.mapmytour.blog.staff.dto.StaffPressIdDTO;
import in.mapmytour.blog.staff.dto.StaffProfileForSelfDTO;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface StaffIdCardService {

    Optional<StaffPressIdDTO> getIdCardDataByIdNumber(String idNumber);

    Optional<StaffProfileForSelfDTO> getIdCardDataForSelf(UUID userId);

    Map<String, Object> getIdCardTemplateModelByIdNumber(String idNumber);

    byte[] generateIdCardPdf(UUID staffId);

    byte[] generateIdCardPng(UUID staffId);

    String getPrintPageUrl(String idNumber);

    String getDownloadPdfUrl(UUID staffId);

    String getIdCardHtmlTemplate();
}
