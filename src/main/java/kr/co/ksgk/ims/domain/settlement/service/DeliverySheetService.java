package kr.co.ksgk.ims.domain.settlement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.settlement.dto.DeliverySheetUploadResponse;
import kr.co.ksgk.ims.domain.settlement.entity.*;
import kr.co.ksgk.ims.domain.settlement.repository.DeliverySheetRowRepository;
import kr.co.ksgk.ims.domain.settlement.repository.SettlementItemRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class DeliverySheetService {

    private final DeliverySheetRowRepository deliverySheetRowRepository;
    private final ProductRepository productRepository;
    private final SettlementItemRepository settlementItemRepository;
    private final SettlementCalculationService settlementCalculationService;
    private final ObjectMapper objectMapper;

    public DeliverySheetService(
            DeliverySheetRowRepository deliverySheetRowRepository,
            ProductRepository productRepository,
            SettlementItemRepository settlementItemRepository,
            @Lazy SettlementCalculationService settlementCalculationService,
            ObjectMapper objectMapper) {
        this.deliverySheetRowRepository = deliverySheetRowRepository;
        this.productRepository = productRepository;
        this.settlementItemRepository = settlementItemRepository;
        this.settlementCalculationService = settlementCalculationService;
        this.objectMapper = objectMapper;
    }

    private static final String PRODUCT_NAME_COLUMN = "상품명";
    private static final String WORK_TYPE_COLUMN = "작업구분";

    @Transactional
    public DeliverySheetUploadResponse uploadDeliverySheet(MultipartFile file, int year, int month) {
        // 기존 데이터 삭제
        deliverySheetRowRepository.deleteByYearAndMonth(year, month);

        // Excel 파싱
        List<ParsedRow> parsedRows = parseExcelFile(file);

        // REMOTE_AREA 항목 조회 (열 이름 매칭용)
        List<SettlementItem> remoteAreaItems = settlementItemRepository.findAll().stream()
                .filter(item -> item.getCalculationType() == CalculationType.REMOTE_AREA)
                .toList();

        // 전체 Product 조회 (name 또는 coupangCode로 매칭)
        List<Product> allProducts = productRepository.findAll();
        Map<String, Product> productByName = new HashMap<>();
        Map<String, Product> productByCoupangCode = new HashMap<>();

        for (Product product : allProducts) {
            productByName.put(product.getName(), product);
            if (product.getCoupangCode() != null && !product.getCoupangCode().isEmpty()) {
                productByCoupangCode.put(product.getCoupangCode(), product);
            }
        }

        List<DeliverySheetUploadResponse.FailedRow> failedRows = new ArrayList<>();
        List<DeliverySheetRow> successRows = new ArrayList<>();

        for (ParsedRow row : parsedRows) {
            // 상품 매칭
            Product matchedProduct = matchProduct(row.productName(), productByName, productByCoupangCode);

            if (matchedProduct == null) {
                failedRows.add(new DeliverySheetUploadResponse.FailedRow(
                        row.rowNumber(), row.productName(), "상품을 찾을 수 없습니다."));
                continue;
            }

            // SIZE/RETURN_SIZE Unit 검증
            WorkType workType = determineWorkType(row.workType(), row.productName(), productByCoupangCode);

            if (workType == WorkType.OUTBOUND && matchedProduct.getSizeUnit() == null) {
                failedRows.add(new DeliverySheetUploadResponse.FailedRow(
                        row.rowNumber(), row.productName(), "출고 사이즈 단가가 설정되지 않았습니다."));
                continue;
            }

            if (workType == WorkType.RETURN && matchedProduct.getReturnSizeUnit() == null) {
                failedRows.add(new DeliverySheetUploadResponse.FailedRow(
                        row.rowNumber(), row.productName(), "반품 사이즈 단가가 설정되지 않았습니다."));
                continue;
            }

            // REMOTE_AREA 비용 JSON 생성
            String remoteAreaFeesJson = buildRemoteAreaFeesJson(row.remoteAreaFees(), remoteAreaItems);

            DeliverySheetRow deliverySheetRow = DeliverySheetRow.create(
                    year, month, row.productName(), matchedProduct, workType, remoteAreaFeesJson);
            successRows.add(deliverySheetRow);
        }

        // 매칭 실패 시 전체 업로드 실패
        if (!failedRows.isEmpty()) {
            return DeliverySheetUploadResponse.failure(year, month, failedRows);
        }

        // 저장
        deliverySheetRowRepository.saveAll(successRows);

        // 정산 계산 트리거
        settlementCalculationService.calculateSettlements(year, month);

        return DeliverySheetUploadResponse.success(year, month, successRows.size());
    }

    private List<ParsedRow> parseExcelFile(MultipartFile file) {
        List<ParsedRow> parsedRows = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }

            // 헤더에서 열 인덱스 찾기
            int productNameIndex = -1;
            int workTypeIndex = -1;
            Map<String, Integer> remoteAreaColumnIndexes = new HashMap<>();

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null) continue;

                String headerName = getCellValueAsString(cell).trim();

                if (PRODUCT_NAME_COLUMN.equals(headerName)) {
                    productNameIndex = i;
                } else if (WORK_TYPE_COLUMN.equals(headerName)) {
                    workTypeIndex = i;
                } else if (!headerName.isEmpty()) {
                    // REMOTE_AREA 열로 간주
                    remoteAreaColumnIndexes.put(headerName, i);
                }
            }

            if (productNameIndex == -1) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }

            // 데이터 행 파싱
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;

                Cell productNameCell = row.getCell(productNameIndex);
                if (productNameCell == null) continue;

                String productName = getCellValueAsString(productNameCell).trim();
                if (productName.isEmpty()) continue;

                String workType = "";
                if (workTypeIndex >= 0) {
                    Cell workTypeCell = row.getCell(workTypeIndex);
                    if (workTypeCell != null) {
                        workType = getCellValueAsString(workTypeCell).trim();
                    }
                }

                // REMOTE_AREA 비용 수집
                Map<String, Integer> remoteAreaFees = new HashMap<>();
                for (Map.Entry<String, Integer> entry : remoteAreaColumnIndexes.entrySet()) {
                    Cell cell = row.getCell(entry.getValue());
                    if (cell != null) {
                        int fee = getCellValueAsInteger(cell);
                        if (fee > 0) {
                            remoteAreaFees.put(entry.getKey(), fee);
                        }
                    }
                }

                parsedRows.add(new ParsedRow(rowNum + 1, productName, workType, remoteAreaFees));
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return parsedRows;
    }

    private Product matchProduct(String productName, Map<String, Product> productByName,
                                  Map<String, Product> productByCoupangCode) {
        // 먼저 coupangCode로 매칭 시도
        Product product = productByCoupangCode.get(productName);
        if (product != null) {
            return product;
        }

        // name으로 매칭 시도
        return productByName.get(productName);
    }

    private WorkType determineWorkType(String workTypeStr, String productName,
                                        Map<String, Product> productByCoupangCode) {
        // coupangCode로 매칭된 경우 RETURN
        if (productByCoupangCode.containsKey(productName)) {
            return WorkType.RETURN;
        }

        // 작업구분이 "반품"인 경우 RETURN
        if ("반품".equals(workTypeStr)) {
            return WorkType.RETURN;
        }

        // 기본값: OUTBOUND (출고)
        return WorkType.OUTBOUND;
    }

    private String buildRemoteAreaFeesJson(Map<String, Integer> remoteAreaFees,
                                            List<SettlementItem> remoteAreaItems) {
        if (remoteAreaFees.isEmpty()) {
            return null;
        }

        // SettlementItem.name과 매칭되는 열만 포함
        Map<String, Integer> matchedFees = new HashMap<>();
        Set<String> remoteAreaItemNames = new HashSet<>();
        for (SettlementItem item : remoteAreaItems) {
            remoteAreaItemNames.add(item.getName());
        }

        for (Map.Entry<String, Integer> entry : remoteAreaFees.entrySet()) {
            if (remoteAreaItemNames.contains(entry.getKey())) {
                matchedFees.put(entry.getKey(), entry.getValue());
            }
        }

        if (matchedFees.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(matchedFees);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize remote area fees", e);
            return null;
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double value = cell.getNumericCellValue();
                if (value == Math.floor(value)) {
                    yield String.valueOf((long) value);
                }
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        yield String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e2) {
                        yield "";
                    }
                }
            }
            default -> "";
        };
    }

    private int getCellValueAsInteger(Cell cell) {
        if (cell == null) {
            return 0;
        }

        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield 0;
                }
            }
            default -> 0;
        };
    }

    private record ParsedRow(int rowNumber, String productName, String workType, Map<String, Integer> remoteAreaFees) {}
}
