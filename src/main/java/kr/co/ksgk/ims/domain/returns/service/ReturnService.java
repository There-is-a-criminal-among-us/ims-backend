package kr.co.ksgk.ims.domain.returns.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
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
import kr.co.ksgk.ims.domain.returns.dto.response.ReturnListResponse;
import kr.co.ksgk.ims.domain.returns.dto.response.ReturnResponse;
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
                returnMall
        );

        return ReturnResponse.from(returnInfo, invoiceRepository);
    }

    public ReturnResponse getReturn(Long returnId) {
        ReturnInfo returnInfo = returnInfoRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        return ReturnResponse.from(returnInfo, invoiceRepository);
    }

    public PagingReturnListResponse getReturnInfosByMember(
            Long memberId,
            String search,
            LocalDate startDate,
            LocalDate endDate,
            ReturnStatus status,
            Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Page<ReturnInfo> returnInfoPage;
        if (member.getRole() == Role.ADMIN) {
            returnInfoPage = returnInfoRepository.findAllWithFilters(startDate, endDate, status, search, pageable);
        } else {
            List<Long> brandIds = getManagedBrandIds(memberId);
            if (brandIds.isEmpty()) {
                return PagingReturnListResponse.of(Page.empty(), List.of());
            }
            returnInfoPage = returnInfoRepository.findByManagedBrandsWithFilters(
                    brandIds, startDate, endDate, status, search, pageable);
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
    public InvoiceUploadSuccessResponse uploadReturnInvoices(List<MultipartFile> files) {
        List<InvoiceUploadErrorResponse.FileErrorDetail> fileErrors = new ArrayList<>();
        Map<String, String> invoiceMap = new HashMap<>();

        for (MultipartFile file : files) {
            try {
                processInvoiceExcelFile(file, invoiceMap, fileErrors);
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

        return InvoiceUploadSuccessResponse.of(files.size(), updatedCount);
    }

    private void processInvoiceExcelFile(MultipartFile file, Map<String, String> invoiceMap,
                                         List<InvoiceUploadErrorResponse.FileErrorDetail> fileErrors) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new BusinessException("헤더 행이 없습니다: " + file.getOriginalFilename(), ErrorCode.BAD_REQUEST);
            }

            int returnInvoiceColumnIndex = findColumnIndex(headerRow, "반송장번호");
            int originalInvoiceColumnIndex = findColumnIndex(headerRow, "원송장번호");

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

                returnInvoice = returnInvoice.trim();
                originalInvoice = originalInvoice.trim();

                if (!returnInfoRepository.findByOriginalInvoice(originalInvoice).isPresent()) {
                    if (!notFoundInvoices.contains(originalInvoice)) {
                        notFoundInvoices.add(originalInvoice);
                    }
                } else {
                    invoiceMap.put(originalInvoice, returnInvoice);
                }
            }

            if (!notFoundInvoices.isEmpty()) {
                fileErrors.add(InvoiceUploadErrorResponse.FileErrorDetail.builder()
                        .fileName(file.getOriginalFilename())
                        .errorType("INVOICE_NOT_FOUND")
                        .errorMessage("등록되지 않은 원송장이 있습니다")
                        .notFoundInvoices(notFoundInvoices)
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

    private int updateReturnInvoices(Map<String, String> invoiceMap) {
        int updatedCount = 0;

        for (Map.Entry<String, String> entry : invoiceMap.entrySet()) {
            String originalInvoice = entry.getKey();
            String returnInvoice = entry.getValue();

            Optional<ReturnInfo> returnInfoOpt = returnInfoRepository.findByOriginalInvoice(originalInvoice);
            if (returnInfoOpt.isPresent()) {
                ReturnInfo returnInfo = returnInfoOpt.get();
                returnInfo.patch(null, null, null, null, null, null, null, null, null, returnInvoice, null, null, null);
                updatedCount++;
            }
        }

        return updatedCount;
    }
}
