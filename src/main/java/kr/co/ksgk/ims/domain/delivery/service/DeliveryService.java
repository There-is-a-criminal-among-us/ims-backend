package kr.co.ksgk.ims.domain.delivery.service;

import kr.co.ksgk.ims.domain.delivery.dto.response.DeliveryResponse;
import kr.co.ksgk.ims.domain.delivery.dto.response.ExcelUploadErrorResponse;
import kr.co.ksgk.ims.domain.delivery.dto.response.ExcelUploadSuccessResponse;
import kr.co.ksgk.ims.domain.delivery.dto.response.PagingDeliveryResponse;
import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import kr.co.ksgk.ims.domain.delivery.exception.ExcelValidationException;
import kr.co.ksgk.ims.domain.delivery.repository.DeliveryRepository;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import kr.co.ksgk.ims.domain.product.repository.RawProductRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RawProductRepository rawProductRepository;

    public PagingDeliveryResponse getAllDeliveries(String search, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Delivery> pageDelivery = deliveryRepository.searchDeliveries(search, startDate, endDate, pageable);
        List<DeliveryResponse> deliveryResponses = pageDelivery.getContent().stream()
                .map(DeliveryResponse::from)
                .collect(Collectors.toList());
        return PagingDeliveryResponse.of(pageDelivery, deliveryResponses);
    }

    @Transactional
    public ExcelUploadSuccessResponse uploadExcelFiles(List<MultipartFile> files) {
        List<ExcelUploadErrorResponse.FileErrorDetail> fileErrors = new ArrayList<>();
        Map<String, Integer> productQuantityMap = new HashMap<>();

        for (MultipartFile file : files) {
            try {
                processExcelFile(file, productQuantityMap, fileErrors);
            } catch (IOException e) {
                fileErrors.add(ExcelUploadErrorResponse.FileErrorDetail.builder()
                        .fileName(file.getOriginalFilename())
                        .errorType("FILE_READ_ERROR")
                        .errorMessage("파일을 읽을 수 없습니다")
                        .notFoundProducts(List.of())
                        .build());
            } catch (BusinessException e) {
                fileErrors.add(ExcelUploadErrorResponse.FileErrorDetail.builder()
                        .fileName(file.getOriginalFilename())
                        .errorType("HEADER_NOT_FOUND")
                        .errorMessage(e.getMessage())
                        .notFoundProducts(List.of())
                        .build());
            }
        }

        if (!fileErrors.isEmpty()) {
            throw new ExcelValidationException(ExcelUploadErrorResponse.of(fileErrors));
        }

        List<Delivery> deliveries = createDeliveries(productQuantityMap);
        deliveryRepository.saveAll(deliveries);

        return ExcelUploadSuccessResponse.of(files.size(), deliveries.size());
    }

    private void processExcelFile(MultipartFile file, Map<String, Integer> productQuantityMap, List<ExcelUploadErrorResponse.FileErrorDetail> fileErrors) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new BusinessException("헤더 행이 없습니다: " + file.getOriginalFilename(), ErrorCode.BAD_REQUEST);
            }

            int productNameColumnIndex = findColumnIndex(headerRow, "품목명");
            int quantityColumnIndex = findColumnIndex(headerRow, "수량");

            if (productNameColumnIndex == -1 || quantityColumnIndex == -1) {
                throw new BusinessException("필수 헤더(품목명, 수량)를 찾을 수 없습니다: " + file.getOriginalFilename(), ErrorCode.BAD_REQUEST);
            }

            List<String> notFoundProducts = new ArrayList<>();
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String productName = getCellStringValue(row.getCell(productNameColumnIndex));
                Integer quantity = getCellIntegerValue(row.getCell(quantityColumnIndex));

                if (productName == null || productName.trim().isEmpty() || quantity == null) {
                    continue;
                }

                productName = productName.trim();
                
                if (!rawProductRepository.findByName(productName).isPresent()) {
                    if (!notFoundProducts.contains(productName)) {
                        notFoundProducts.add(productName);
                    }
                } else {
                    productQuantityMap.merge(productName, quantity, Integer::sum);
                }
            }

            if (!notFoundProducts.isEmpty()) {
                fileErrors.add(ExcelUploadErrorResponse.FileErrorDetail.builder()
                        .fileName(file.getOriginalFilename())
                        .errorType("PRODUCT_NOT_FOUND")
                        .errorMessage("등록되지 않은 품목명이 있습니다")
                        .notFoundProducts(notFoundProducts)
                        .build());
            }
        }
    }

    private int findColumnIndex(Row headerRow, String headerName) {
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String cellValue = getCellStringValue(cell);
                if (cellValue != null && cellValue.trim().equals(headerName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private Integer getCellIntegerValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private List<Delivery> createDeliveries(Map<String, Integer> productQuantityMap) {
        List<Delivery> deliveries = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : productQuantityMap.entrySet()) {
            String productName = entry.getKey();
            Integer quantity = entry.getValue();
            
            RawProduct rawProduct = rawProductRepository.findByName(productName)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RAW_PRODUCT_NOT_FOUND));
            
            deliveries.add(Delivery.builder()
                    .quantity(quantity)
                    .rawProduct(rawProduct)
                    .build());
        }
        
        return deliveries;
    }
}