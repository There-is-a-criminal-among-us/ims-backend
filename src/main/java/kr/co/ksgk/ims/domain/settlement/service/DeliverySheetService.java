package kr.co.ksgk.ims.domain.settlement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import kr.co.ksgk.ims.domain.product.repository.ProductMappingRepository;
import kr.co.ksgk.ims.domain.product.repository.RawProductRepository;
import kr.co.ksgk.ims.domain.settlement.dto.response.DeliverySheetRemoteAreaListResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.DeliverySheetRemoteAreaResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.DeliverySheetReturnListResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.DeliverySheetReturnResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.DeliverySheetUploadResponse;
import kr.co.ksgk.ims.domain.settlement.entity.*;
import kr.co.ksgk.ims.domain.settlement.repository.DeliverySheetRemoteAreaRepository;
import kr.co.ksgk.ims.domain.settlement.repository.DeliverySheetReturnRepository;
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
    private final DeliverySheetReturnRepository deliverySheetReturnRepository;
    private final DeliverySheetRemoteAreaRepository deliverySheetRemoteAreaRepository;
    private final RawProductRepository rawProductRepository;
    private final ProductMappingRepository productMappingRepository;
    private final SettlementItemRepository settlementItemRepository;
    private final SettlementCalculationService settlementCalculationService;
    private final ObjectMapper objectMapper;

    public DeliverySheetService(
            DeliverySheetRowRepository deliverySheetRowRepository,
            DeliverySheetReturnRepository deliverySheetReturnRepository,
            DeliverySheetRemoteAreaRepository deliverySheetRemoteAreaRepository,
            RawProductRepository rawProductRepository,
            ProductMappingRepository productMappingRepository,
            SettlementItemRepository settlementItemRepository,
            @Lazy SettlementCalculationService settlementCalculationService,
            ObjectMapper objectMapper) {
        this.deliverySheetRowRepository = deliverySheetRowRepository;
        this.deliverySheetReturnRepository = deliverySheetReturnRepository;
        this.deliverySheetRemoteAreaRepository = deliverySheetRemoteAreaRepository;
        this.rawProductRepository = rawProductRepository;
        this.productMappingRepository = productMappingRepository;
        this.settlementItemRepository = settlementItemRepository;
        this.settlementCalculationService = settlementCalculationService;
        this.objectMapper = objectMapper;
    }

    private static final String PRODUCT_NAME_COLUMN = "상품명";
    private static final String WORK_TYPE_COLUMN = "작업구분";
    private static final String PICKUP_DATE_COLUMN = "집하일자";
    private static final String INVOICE_NUMBER_COLUMN = "운송장번호";
    private static final String SENDER_NAME_COLUMN = "송하인명";
    private static final String RECEIVER_NAME_COLUMN = "수하인명";
    private static final String RECEIVER_ADDRESS_COLUMN = "수하인주소";
    private static final Set<String> REMOTE_AREA_COLUMNS = Set.of(
            "제주연계",
            "집하도선료",
            "배달도선료",
            "집하산간",
            "배달산간"
    );

    @Transactional
    public DeliverySheetUploadResponse uploadDeliverySheet(MultipartFile file, int year, int month) {
        // 기존 데이터 삭제
        deliverySheetRowRepository.deleteByYearAndMonth(year, month);
        deliverySheetReturnRepository.deleteByYearAndMonth(year, month);
        deliverySheetRemoteAreaRepository.deleteByYearAndMonth(year, month);

        // Excel 파싱
        List<ParsedRow> parsedRows = parseExcelFile(file);

        // REMOTE_AREA 항목 조회 (열 이름 매칭용)
        List<SettlementItem> remoteAreaItems = settlementItemRepository.findAll().stream()
                .filter(item -> item.getCalculationType() == CalculationType.REMOTE_AREA)
                .toList();

        // 전체 RawProduct 조회 (name 또는 coupangCode로 매칭)
        List<RawProduct> allRawProducts = rawProductRepository.findAll();
        Map<String, RawProduct> rawProductByName = new HashMap<>();
        Map<String, RawProduct> rawProductByCoupangCode = new HashMap<>();

        for (RawProduct rawProduct : allRawProducts) {
            rawProductByName.put(rawProduct.getName(), rawProduct);
            if (rawProduct.getCoupangCode() != null && !rawProduct.getCoupangCode().isEmpty()) {
                rawProductByCoupangCode.put(rawProduct.getCoupangCode(), rawProduct);
            }
        }

        List<DeliverySheetUploadResponse.FailedRow> failedRows = new ArrayList<>();
        List<DeliverySheetRow> successRows = new ArrayList<>();
        List<DeliverySheetReturn> returnRows = new ArrayList<>();
        List<DeliverySheetRemoteArea> remoteAreaRows = new ArrayList<>();

        for (ParsedRow row : parsedRows) {
            // RawProduct 매칭
            RawProduct matchedRawProduct = matchRawProduct(row.productName(),
                    rawProductByName, rawProductByCoupangCode);

            if (matchedRawProduct == null) {
                failedRows.add(new DeliverySheetUploadResponse.FailedRow(
                        row.rowNumber(), row.productName(), "상품을 찾을 수 없습니다."));
                continue;
            }

            // WorkType 결정
            WorkType workType = determineWorkType(row.workType(), row.productName(),
                    rawProductByCoupangCode);

            // SizeUnit 검증 (RawProduct 기준)
            if (workType == WorkType.OUTBOUND && matchedRawProduct.getSizeUnit() == null) {
                failedRows.add(new DeliverySheetUploadResponse.FailedRow(
                        row.rowNumber(), row.productName(), "출고 사이즈 단가가 설정되지 않았습니다."));
                continue;
            }

            if (workType == WorkType.RETURN && matchedRawProduct.getReturnSizeUnit() == null) {
                failedRows.add(new DeliverySheetUploadResponse.FailedRow(
                        row.rowNumber(), row.productName(), "반품 사이즈 단가가 설정되지 않았습니다."));
                continue;
            }

            // 매핑된 Products 조회
            List<ProductMapping> mappings = productMappingRepository.findByRawProduct(matchedRawProduct);
            if (mappings.isEmpty()) {
                failedRows.add(new DeliverySheetUploadResponse.FailedRow(
                        row.rowNumber(), row.productName(), "매핑된 상품이 없습니다."));
                continue;
            }

            // REMOTE_AREA 비용 JSON 생성
            String remoteAreaFeesJson = buildRemoteAreaFeesJson(row.remoteAreaFees(), remoteAreaItems);

            // 매핑된 모든 Product에 DeliverySheetRow 생성
            boolean isFirst = true;
            for (ProductMapping mapping : mappings) {
                Product product = mapping.getProduct();
                DeliverySheetRow deliverySheetRow = DeliverySheetRow.create(
                        year, month, row.productName(), product, workType,
                        isFirst ? remoteAreaFeesJson : null,
                        isFirst, mapping.getQuantity());
                successRows.add(deliverySheetRow);
                isFirst = false;
            }

            // 반품내역 저장 (작업구분 반품 또는 쿠팡코드 매칭 등 RETURN 처리)
            if (workType == WorkType.RETURN) {
                int amount = matchedRawProduct.getReturnSizeUnit().getPrice();
                returnRows.add(DeliverySheetReturn.create(
                        year,
                        month,
                        row.pickupDate(),
                        row.invoiceNumber(),
                        row.workType(),
                        row.senderName(),
                        row.receiverName(),
                        row.productName(),
                        amount
                ));
            }

            // 도서산간 목록 저장 (운임합계 > 0)
            int remoteAreaTotal = row.remoteAreaFees().values().stream().mapToInt(Integer::intValue).sum();
            if (remoteAreaTotal > 0) {
                remoteAreaRows.add(DeliverySheetRemoteArea.create(
                        year,
                        month,
                        row.pickupDate(),
                        row.invoiceNumber(),
                        row.senderName(),
                        row.receiverName(),
                        row.receiverAddress(),
                        row.productName(),
                        remoteAreaTotal
                ));
            }
        }

        // 매칭 실패 시 전체 업로드 실패
        if (!failedRows.isEmpty()) {
            return DeliverySheetUploadResponse.failure(year, month, failedRows);
        }

        // 저장
        deliverySheetRowRepository.saveAll(successRows);
        deliverySheetReturnRepository.saveAll(returnRows);
        deliverySheetRemoteAreaRepository.saveAll(remoteAreaRows);

        // 정산 계산 트리거
        settlementCalculationService.calculateSettlements(year, month);

        return DeliverySheetUploadResponse.success(year, month, successRows.size());
    }

    public DeliverySheetReturnListResponse getDeliverySheetReturns(int year, int month) {
        List<DeliverySheetReturnResponse> items = deliverySheetReturnRepository.findByYearAndMonthOrderByIdAsc(year, month).stream()
                .map(DeliverySheetReturnResponse::from)
                .toList();
        return DeliverySheetReturnListResponse.of(items);
    }

    public DeliverySheetRemoteAreaListResponse getDeliverySheetRemoteAreas(int year, int month) {
        List<DeliverySheetRemoteAreaResponse> items = deliverySheetRemoteAreaRepository.findByYearAndMonthOrderByIdAsc(year, month).stream()
                .map(DeliverySheetRemoteAreaResponse::from)
                .toList();
        return DeliverySheetRemoteAreaListResponse.of(items);
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
            int pickupDateIndex = -1;
            int invoiceNumberIndex = -1;
            int senderNameIndex = -1;
            int receiverNameIndex = -1;
            int receiverAddressIndex = -1;
            Map<String, Integer> remoteAreaColumnIndexes = new HashMap<>();

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null) continue;

                String headerName = getCellValueAsString(cell).trim();

                if (PRODUCT_NAME_COLUMN.equals(headerName)) {
                    productNameIndex = i;
                } else if (WORK_TYPE_COLUMN.equals(headerName)) {
                    workTypeIndex = i;
                } else if (PICKUP_DATE_COLUMN.equals(headerName)) {
                    pickupDateIndex = i;
                } else if (INVOICE_NUMBER_COLUMN.equals(headerName)) {
                    invoiceNumberIndex = i;
                } else if (SENDER_NAME_COLUMN.equals(headerName)) {
                    senderNameIndex = i;
                } else if (RECEIVER_NAME_COLUMN.equals(headerName)) {
                    receiverNameIndex = i;
                } else if (RECEIVER_ADDRESS_COLUMN.equals(headerName)) {
                    receiverAddressIndex = i;
                } else if (REMOTE_AREA_COLUMNS.contains(headerName)) {
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

                String pickupDate = "";
                if (pickupDateIndex >= 0) {
                    pickupDate = getCellValueAsString(row.getCell(pickupDateIndex)).trim();
                }

                String invoiceNumber = "";
                if (invoiceNumberIndex >= 0) {
                    invoiceNumber = getCellValueAsString(row.getCell(invoiceNumberIndex)).trim();
                }

                String senderName = "";
                if (senderNameIndex >= 0) {
                    senderName = getCellValueAsString(row.getCell(senderNameIndex)).trim();
                }

                String receiverName = "";
                if (receiverNameIndex >= 0) {
                    receiverName = getCellValueAsString(row.getCell(receiverNameIndex)).trim();
                }

                String receiverAddress = "";
                if (receiverAddressIndex >= 0) {
                    receiverAddress = getCellValueAsString(row.getCell(receiverAddressIndex)).trim();
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

                parsedRows.add(new ParsedRow(rowNum + 1, productName, workType, pickupDate, invoiceNumber,
                        senderName, receiverName, receiverAddress, remoteAreaFees));
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return parsedRows;
    }

    private RawProduct matchRawProduct(String productName,
                                       Map<String, RawProduct> rawProductByName,
                                       Map<String, RawProduct> rawProductByCoupangCode) {
        // 먼저 coupangCode로 매칭 시도
        RawProduct rawProduct = rawProductByCoupangCode.get(productName);
        if (rawProduct != null) {
            return rawProduct;
        }

        // RawProduct.name으로 매칭 시도
        return rawProductByName.get(productName);
    }

    private WorkType determineWorkType(String workTypeStr, String productName,
                                        Map<String, RawProduct> rawProductByCoupangCode) {
        // coupangCode로 매칭된 경우 RETURN
        if (rawProductByCoupangCode.containsKey(productName)) {
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
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
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

    private record ParsedRow(int rowNumber,
                             String productName,
                             String workType,
                             String pickupDate,
                             String invoiceNumber,
                             String senderName,
                             String receiverName,
                             String receiverAddress,
                             Map<String, Integer> remoteAreaFees) {}
}
