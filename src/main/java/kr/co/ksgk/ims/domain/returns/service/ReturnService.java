package kr.co.ksgk.ims.domain.returns.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceRepository;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.Role;
import kr.co.ksgk.ims.domain.member.repository.MemberBrandRepository;
import kr.co.ksgk.ims.domain.member.repository.MemberCompanyRepository;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.returns.dto.request.CreateReturnRequest;
import kr.co.ksgk.ims.domain.returns.dto.request.PatchReturnRequest;
import kr.co.ksgk.ims.domain.returns.dto.response.InvoiceUploadErrorResponse;
import kr.co.ksgk.ims.domain.returns.dto.response.InvoiceUploadSuccessResponse;
import kr.co.ksgk.ims.domain.returns.dto.response.PagingReturnListResponse;
import kr.co.ksgk.ims.domain.returns.dto.response.ReturnBatchResponse;
import kr.co.ksgk.ims.domain.returns.dto.response.ReturnExcelUploadResponse;
import kr.co.ksgk.ims.domain.returns.dto.response.ReturnListResponse;
import kr.co.ksgk.ims.domain.returns.dto.response.ReturnResponse;
import kr.co.ksgk.ims.domain.returns.entity.ProcessingStatus;
import kr.co.ksgk.ims.domain.returns.entity.ReturnHandler;
import kr.co.ksgk.ims.domain.returns.entity.ReturnInfo;
import kr.co.ksgk.ims.domain.returns.entity.ReturnMall;
import kr.co.ksgk.ims.domain.returns.entity.ReturnStatus;
import kr.co.ksgk.ims.domain.returns.exception.InvoiceValidationException;
import kr.co.ksgk.ims.domain.returns.repository.ReturnHandlerRepository;
import kr.co.ksgk.ims.domain.returns.repository.ReturnInfoRepository;
import kr.co.ksgk.ims.domain.returns.repository.ReturnMallRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import kr.co.ksgk.ims.global.error.exception.UnauthorizedException;
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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReturnService {

    private final ReturnHandlerRepository returnHandlerRepository;
    private final ReturnMallRepository returnMallRepository;
    private final ReturnInfoRepository returnInfoRepository;
    private final MemberRepository memberRepository;
    private final MemberBrandRepository memberBrandRepository;
    private final MemberCompanyRepository memberCompanyRepository;
    private final InvoiceRepository invoiceRepository;

    @Transactional
    public ReturnResponse createReturn(CreateReturnRequest request) {
        ReturnHandler returnHandler = returnHandlerRepository.findById(request.handlerId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        ReturnMall returnMall = returnMallRepository.findById(request.mallId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        ReturnInfo returnInfo = request.toEntity(returnHandler, returnMall);
        ReturnInfo savedReturnInfo = returnInfoRepository.save(returnInfo);
        return ReturnResponse.from(savedReturnInfo, invoiceRepository);
    }

    @Transactional
    public ReturnResponse reRequestReturn(Long returnId) {
        ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        returnInfo.reRequest();
        return ReturnResponse.from(returnInfo, invoiceRepository);
    }

    @Transactional
    public void bulkReRequestReturns(Long memberId, List<Long> returnIds) {
        for (Long returnId : returnIds) {
            validateReturnInfoAccess(memberId, returnId);

            ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));

            returnInfo.reRequest();
        }
    }

    @Transactional
    public void bulkUpdateProcessingStatus(Long memberId, List<Long> returnIds, ProcessingStatus processingStatus) {
        for (Long returnId : returnIds) {
            validateReturnInfoAccess(memberId, returnId);

            ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));

            returnInfo.patch(null, null, null, null, null, null, null, null, null, null, null, null, null, null, processingStatus);
        }
    }

    @Transactional
    public ReturnResponse patchReturn(Long memberId, Long returnId, PatchReturnRequest request) {
        ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));

        validateReturnInfoAccess(memberId, returnId);

        ReturnHandler returnHandler = null;
        if (request.handlerId() != null) {
            returnHandler = returnHandlerRepository.findById(request.handlerId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        }

        ReturnMall returnMall = null;
        if (request.mallId() != null) {
            returnMall = returnMallRepository.findById(request.mallId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        }

        returnInfo.patch(
                request.buyer(),
                request.receiver(),
                request.address(),
                request.phone(),
                request.productName(),
                request.quantity(),
                request.originalInvoice(),
                request.acceptDate(),
                request.returnStatus(),
                request.returnInvoice(),
                request.note(),
                returnHandler,
                returnMall,
                request.orderType(),
                request.processingStatus()
        );

        return ReturnResponse.from(returnInfo, invoiceRepository);
    }

    public ReturnResponse getReturn(Long returnId) {
        ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        return ReturnResponse.from(returnInfo, invoiceRepository);
    }

    public List<ReturnBatchResponse> getReturnBatchInfo(Long memberId, List<Long> returnIds) {
        List<ReturnBatchResponse> responses = new ArrayList<>();

        for (Long returnId : returnIds) {
            validateReturnInfoAccess(memberId, returnId);

            ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));

            Integer resalableQuantity = null;
            if (returnInfo.getReturnInvoice() != null && !returnInfo.getReturnInvoice().trim().isEmpty()) {
                Optional<Invoice> invoiceOpt = invoiceRepository.findByNumber(returnInfo.getReturnInvoice());
                if (invoiceOpt.isPresent()) {
                    Invoice invoice = invoiceOpt.get();
                    resalableQuantity = invoice.getInvoiceProducts().stream()
                            .mapToInt(ip -> ip.getResalableQuantity() != null ? ip.getResalableQuantity() : 0)
                            .sum();
                }
            }

            responses.add(ReturnBatchResponse.from(returnInfo, resalableQuantity));
        }

        return responses;
    }

    public PagingReturnListResponse getReturnInfosByMember(
            Long memberId,
            String search,
            LocalDate startDate,
            LocalDate endDate,
            ReturnStatus status,
            ProcessingStatus processingStatus,
            Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Page<ReturnInfo> returnInfoPage;
        if (member.getRole() == Role.ADMIN) {
            returnInfoPage = returnInfoRepository.findAllWithFilters(startDate, endDate, status, processingStatus, search, pageable);
        } else {
            List<Long> brandIds = getManagedBrandIds(memberId);
            if (brandIds.isEmpty()) {
                return PagingReturnListResponse.of(Page.empty(), List.of());
            }
            returnInfoPage = returnInfoRepository.findByManagedBrandsWithFilters(
                    brandIds, startDate, endDate, status, processingStatus, search, pageable);
        }

        List<ReturnListResponse> returnListResponses = returnInfoPage.getContent().stream()
                .map(returnInfo -> ReturnListResponse.from(returnInfo, invoiceRepository))
                .toList();

        return PagingReturnListResponse.of(returnInfoPage, returnListResponses);
    }

    public void validateReturnInfoAccess(Long memberId, Long returnId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getRole() == Role.ADMIN) {
            return;
        }

        ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));

        Long mallBrandId = returnInfo.getReturnMall().getBrand().getId();
        Long handlerBrandId = returnInfo.getReturnHandler().getBrand().getId();

        List<Long> managedBrandIds = getManagedBrandIds(memberId);

        if (!managedBrandIds.contains(mallBrandId) && !managedBrandIds.contains(handlerBrandId)) {
            throw new UnauthorizedException(ErrorCode.FORBIDDEN);
        }
    }

    private List<Long> getManagedBrandIds(Long memberId) {
        List<Long> directBrandIds = memberBrandRepository.findByMemberId(memberId).stream()
                .map(mb -> mb.getBrand().getId())
                .toList();

        List<Long> companyBrandIds = memberCompanyRepository.findByMemberId(memberId).stream()
                .flatMap(mc -> mc.getCompany().getBrands().stream())
                .map(Brand::getId)
                .toList();

        return Stream.concat(directBrandIds.stream(), companyBrandIds.stream())
                .distinct()
                .toList();
    }

    @Transactional
    public void acceptReturns(Long memberId, List<Long> returnIds) {
        for (Long returnId : returnIds) {
            validateReturnInfoAccess(memberId, returnId);

            ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));

            returnInfo.accept();
        }
    }

    @Transactional
    public void deleteReturn(Long memberId, Long returnId) {
        validateReturnInfoAccess(memberId, returnId);

        ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));

        returnInfoRepository.delete(returnInfo);
    }

    @Transactional
    public InvoiceUploadSuccessResponse uploadReturnInvoices(List<MultipartFile> files) {
        List<InvoiceUploadErrorResponse.FileErrorDetail> fileErrors = new ArrayList<>();
        Map<String, String> invoiceMap = new HashMap<>();
        List<String> allNotFoundInvoices = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                List<String> notFoundInvoices = processInvoiceExcelFile(file, invoiceMap, fileErrors);
                allNotFoundInvoices.addAll(notFoundInvoices);
            } catch (IOException e) {
                fileErrors.add(InvoiceUploadErrorResponse.FileErrorDetail.builder()
                        .fileName(file.getOriginalFilename())
                        .errorType("FILE_READ_ERROR")
                        .errorMessage("파일을 읽을 수 없습니다")
                        .notFoundInvoices(List.of())
                        .build());
            } catch (BusinessException e) {
                fileErrors.add(InvoiceUploadErrorResponse.FileErrorDetail.builder()
                        .fileName(file.getOriginalFilename())
                        .errorType("HEADER_NOT_FOUND")
                        .errorMessage(e.getMessage())
                        .notFoundInvoices(List.of())
                        .build());
            }
        }

        if (!fileErrors.isEmpty()) {
            throw new InvoiceValidationException(InvoiceUploadErrorResponse.of(fileErrors));
        }

        int updatedCount = updateReturnInvoices(invoiceMap);

        return InvoiceUploadSuccessResponse.of(files.size(), updatedCount, allNotFoundInvoices);
    }

    private List<String> processInvoiceExcelFile(MultipartFile file, Map<String, String> invoiceMap,
                                         List<InvoiceUploadErrorResponse.FileErrorDetail> fileErrors) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new BusinessException("헤더 행이 없습니다: " + file.getOriginalFilename(), ErrorCode.BAD_REQUEST);
            }

            int returnInvoiceColumnIndex = findColumnIndex(headerRow, "반송장번호");
            int originalInvoiceColumnIndex = findColumnIndex(headerRow, "원송장번호");

            if (originalInvoiceColumnIndex == -1) {
                originalInvoiceColumnIndex = findColumnIndex(headerRow, "운송장번호");
            }

            if (returnInvoiceColumnIndex == -1 || originalInvoiceColumnIndex == -1) {
                throw new BusinessException("필수 헤더를 찾을 수 없습니다: " + file.getOriginalFilename(), ErrorCode.BAD_REQUEST);
            }

            List<String> notFoundInvoices = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String returnInvoice = getCellStringValue(row.getCell(returnInvoiceColumnIndex));
                String originalInvoice = getCellStringValue(row.getCell(originalInvoiceColumnIndex));

                if (returnInvoice == null || returnInvoice.trim().isEmpty() ||
                        originalInvoice == null || originalInvoice.trim().isEmpty()) {
                    continue;
                }

                returnInvoice = normalizeInvoiceNumber(returnInvoice);
                originalInvoice = normalizeInvoiceNumber(originalInvoice);

                if (returnInvoice.isEmpty() || originalInvoice.isEmpty()) {
                    continue;
                }

                Optional<ReturnInfo> returnInfoOpt = findReturnInfoByNormalizedInvoice(originalInvoice);
                if (!returnInfoOpt.isPresent()) {
                    if (!notFoundInvoices.contains(originalInvoice)) {
                        notFoundInvoices.add(originalInvoice);
                    }
                } else {
                    invoiceMap.put(originalInvoice, returnInvoice);
                }
            }

            return notFoundInvoices;
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

    private String normalizeInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber == null) return null;
        return invoiceNumber.replaceAll("-", "").trim();
    }

    private Optional<ReturnInfo> findReturnInfoByNormalizedInvoice(String normalizedInvoice) {
        List<ReturnInfo> allReturnInfos = returnInfoRepository.findAll();
        return allReturnInfos.stream()
                .filter(returnInfo -> {
                    String dbInvoice = normalizeInvoiceNumber(returnInfo.getOriginalInvoice());
                    return dbInvoice != null && dbInvoice.equals(normalizedInvoice);
                })
                .findFirst();
    }

    private int updateReturnInvoices(Map<String, String> invoiceMap) {
        int updatedCount = 0;

        for (Map.Entry<String, String> entry : invoiceMap.entrySet()) {
            String normalizedOriginalInvoice = entry.getKey();
            String returnInvoice = entry.getValue();

            Optional<ReturnInfo> returnInfoOpt = findReturnInfoByNormalizedInvoice(normalizedOriginalInvoice);
            if (returnInfoOpt.isPresent()) {
                ReturnInfo returnInfo = returnInfoOpt.get();
                returnInfo.patch(null, null, null, null, null, null, null, null, null, returnInvoice, null, null, null, null, null);
                updatedCount++;
            }
        }

        return updatedCount;
    }

    @Transactional
    public ReturnExcelUploadResponse uploadReturnExcel(List<MultipartFile> files) {
        int totalFiles = files.size();
        int successFiles = 0;
        int totalRows = 0;
        int successCount = 0;
        List<ReturnExcelUploadResponse.FileErrorSummary> errorDetails = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                List<String> fileErrors = new ArrayList<>();
                int[] counts = processReturnExcelFile(file, fileErrors);

                if (!fileErrors.isEmpty()) {
                    // 파일에 에러가 있으면 해당 파일 전체 스킵
                    errorDetails.add(ReturnExcelUploadResponse.FileErrorSummary.builder()
                            .fileName(file.getOriginalFilename())
                            .errorMessage("파일 처리 중 오류 발생")
                            .errorRows(fileErrors)
                            .build());
                } else {
                    // 에러 없으면 성공
                    totalRows += counts[0];
                    successCount += counts[1];
                    successFiles++;
                }
            } catch (IOException e) {
                errorDetails.add(ReturnExcelUploadResponse.FileErrorSummary.builder()
                        .fileName(file.getOriginalFilename())
                        .errorMessage("파일을 읽을 수 없습니다: " + e.getMessage())
                        .errorRows(List.of())
                        .build());
            }
        }

        int errorFiles = totalFiles - successFiles;
        return ReturnExcelUploadResponse.of(totalFiles, successFiles, errorFiles, totalRows, successCount, errorDetails);
    }

    private int[] processReturnExcelFile(MultipartFile file, List<String> fileErrors) throws IOException {
        int totalRows = 0;
        int successCount = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                fileErrors.add("행 0: 헤더 행이 없습니다");
                return new int[]{0, 0};
            }

            Map<String, Integer> columnIndexMap = new HashMap<>();
            columnIndexMap.put("구매자", findColumnIndex(headerRow, "구매자"));
            columnIndexMap.put("수취인", findColumnIndex(headerRow, "수취인"));
            columnIndexMap.put("주소", findColumnIndex(headerRow, "주소"));
            columnIndexMap.put("전화번호", findColumnIndex(headerRow, "전화번호"));
            columnIndexMap.put("상품명", findColumnIndex(headerRow, "상품명"));
            columnIndexMap.put("수량", findColumnIndex(headerRow, "수량"));
            columnIndexMap.put("원송장번호", findColumnIndex(headerRow, "원송장번호"));
            columnIndexMap.put("비고", findColumnIndex(headerRow, "비고"));
            columnIndexMap.put("접수자", findColumnIndex(headerRow, "접수자"));
            columnIndexMap.put("쇼핑몰", findColumnIndex(headerRow, "쇼핑몰"));
            columnIndexMap.put("주문유형", findColumnIndex(headerRow, "주문유형"));

            if (columnIndexMap.get("원송장번호") == -1) {
                columnIndexMap.put("원송장번호", findColumnIndex(headerRow, "운송장번호"));
            }

            List<String> missingColumns = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
                if (!entry.getKey().equals("비고") && entry.getValue() == -1) {
                    missingColumns.add(entry.getKey());
                }
            }

            if (!missingColumns.isEmpty()) {
                fileErrors.add("행 0: 필수 헤더를 찾을 수 없습니다 - " + String.join(", ", missingColumns));
                return new int[]{0, 0};
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalRows++;

                try {
                    String buyer = getCellStringValue(row.getCell(columnIndexMap.get("구매자")));
                    String receiver = getCellStringValue(row.getCell(columnIndexMap.get("수취인")));
                    String address = getCellStringValue(row.getCell(columnIndexMap.get("주소")));
                    String phone = getCellStringValue(row.getCell(columnIndexMap.get("전화번호")));
                    String productName = getCellStringValue(row.getCell(columnIndexMap.get("상품명")));
                    String quantityStr = getCellStringValue(row.getCell(columnIndexMap.get("수량")));
                    String originalInvoice = getCellStringValue(row.getCell(columnIndexMap.get("원송장번호")));
                    String note = columnIndexMap.get("비고") != -1
                            ? getCellStringValue(row.getCell(columnIndexMap.get("비고")))
                            : null;
                    String handlerName = getCellStringValue(row.getCell(columnIndexMap.get("접수자")));
                    String mallName = getCellStringValue(row.getCell(columnIndexMap.get("쇼핑몰")));
                    String orderTypeStr = getCellStringValue(row.getCell(columnIndexMap.get("주문유형")));

                    if (buyer == null || buyer.trim().isEmpty() ||
                            receiver == null || receiver.trim().isEmpty() ||
                            address == null || address.trim().isEmpty() ||
                            phone == null || phone.trim().isEmpty() ||
                            productName == null || productName.trim().isEmpty() ||
                            quantityStr == null || quantityStr.trim().isEmpty() ||
                            originalInvoice == null || originalInvoice.trim().isEmpty() ||
                            handlerName == null || handlerName.trim().isEmpty() ||
                            mallName == null || mallName.trim().isEmpty() ||
                            orderTypeStr == null || orderTypeStr.trim().isEmpty()) {
                        fileErrors.add("행 " + (i + 1) + ": 필수 필드가 비어있습니다");
                        continue;
                    }

                    Integer quantity;
                    try {
                        quantity = Integer.parseInt(quantityStr.trim());
                    } catch (NumberFormatException e) {
                        fileErrors.add("행 " + (i + 1) + ": 수량은 숫자여야 합니다 - " + quantityStr);
                        continue;
                    }

                    List<ReturnHandler> handlers = returnHandlerRepository.findAll();
                    ReturnHandler handler = handlers.stream()
                            .filter(h -> h.getName().equals(handlerName.trim()))
                            .findFirst()
                            .orElse(null);

                    if (handler == null) {
                        fileErrors.add("행 " + (i + 1) + ": 존재하지 않는 접수자입니다 - " + handlerName);
                        continue;
                    }

                    List<ReturnMall> malls = returnMallRepository.findAll();
                    ReturnMall mall = malls.stream()
                            .filter(m -> m.getName().equals(mallName.trim()))
                            .findFirst()
                            .orElse(null);

                    if (mall == null) {
                        fileErrors.add("행 " + (i + 1) + ": 존재하지 않는 쇼핑몰입니다 - " + mallName);
                        continue;
                    }

                    kr.co.ksgk.ims.domain.returns.entity.OrderType orderType = parseOrderType(orderTypeStr);
                    if (orderType == null) {
                        fileErrors.add("행 " + (i + 1) + ": 잘못된 주문유형입니다 - " + orderTypeStr + " (허용: 반품신청, 교환신청, 기타)");
                        continue;
                    }

                    // 원송장 중복 체크
                    String normalizedOriginalInvoice = normalizeInvoiceNumber(originalInvoice.trim());
                    Optional<ReturnInfo> existingReturn = findReturnInfoByNormalizedInvoice(normalizedOriginalInvoice);
                    if (existingReturn.isPresent()) {
                        fileErrors.add("행 " + (i + 1) + ": 원송장번호가 이미 존재합니다 - " + originalInvoice.trim());
                        continue;
                    }

                    ReturnInfo returnInfo = ReturnInfo.builder()
                            .buyer(buyer.trim())
                            .receiver(receiver.trim())
                            .address(address.trim())
                            .phone(phone.trim())
                            .productName(productName.trim())
                            .quantity(quantity)
                            .originalInvoice(originalInvoice.trim())
                            .note(note != null ? note.trim() : null)
                            .returnHandler(handler)
                            .returnMall(mall)
                            .orderType(orderType)
                            .build();

                    returnInfoRepository.save(returnInfo);
                    successCount++;

                } catch (Exception e) {
                    fileErrors.add("행 " + (i + 1) + ": 데이터 처리 중 오류 발생 - " + e.getMessage());
                }
            }
        }

        return new int[]{totalRows, successCount};
    }

    private String getRowData(Row row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (i > 0) sb.append(" | ");
            String cellValue = getCellStringValue(row.getCell(i));
            sb.append(cellValue != null ? cellValue : "");
        }
        return sb.toString();
    }

    private kr.co.ksgk.ims.domain.returns.entity.OrderType parseOrderType(String orderTypeStr) {
        if (orderTypeStr == null || orderTypeStr.trim().isEmpty()) {
            return null;
        }

        String normalized = orderTypeStr.trim();

        switch (normalized) {
            case "반품신청":
            case "RETURN":
                return kr.co.ksgk.ims.domain.returns.entity.OrderType.RETURN;
            case "교환신청":
            case "EXCHANGE":
                return kr.co.ksgk.ims.domain.returns.entity.OrderType.EXCHANGE;
            case "기타":
            case "ETC":
                return kr.co.ksgk.ims.domain.returns.entity.OrderType.ETC;
            default:
                return null;
        }
    }
}
