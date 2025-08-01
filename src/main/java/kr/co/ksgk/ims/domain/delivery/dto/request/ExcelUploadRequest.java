package kr.co.ksgk.ims.domain.delivery.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ExcelUploadRequest(
        List<MultipartFile> files
) {
}