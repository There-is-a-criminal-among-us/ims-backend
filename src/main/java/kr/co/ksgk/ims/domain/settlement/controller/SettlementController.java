package kr.co.ksgk.ims.domain.settlement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.auth.dto.AuthDto;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.settlement.dto.*;
import kr.co.ksgk.ims.domain.settlement.dto.request.SettlementDetailUpdateRequest;
import kr.co.ksgk.ims.domain.settlement.entity.CalculationType;
import kr.co.ksgk.ims.domain.settlement.service.DeliverySheetService;
import kr.co.ksgk.ims.domain.settlement.service.SettlementManagementService;
import kr.co.ksgk.ims.domain.settlement.service.SettlementService;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settlements")
@Tag(name = "Settlement", description = "정산서 API")
public class SettlementController {

    private final SettlementService settlementService;
    private final DeliverySheetService deliverySheetService;
    private final SettlementManagementService settlementManagementService;
    private final MemberRepository memberRepository;

    @GetMapping("/charge-categories")
    @Operation(summary = "청구 카테고리 목록 조회", description = "모든 청구 카테고리를 조회합니다")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChargeCategoryDto.class)))
    public ResponseEntity<SuccessResponse<?>> getChargeCategories() {
        List<ChargeCategoryDto> response = settlementService.getChargeCategories();
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/structure")
    @Operation(summary = "정산서 구조 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementStructureDto.class)))
    public ResponseEntity<SuccessResponse<?>> getSettlementStructure() {
        SettlementStructureDto response = settlementService.getSettlementStructure();
        return SuccessResponse.ok(response);
    }

    @GetMapping("/units")
    @Operation(summary = "정산 단위 목록 조회", description = "CalculationType에 따른 정산 단위 목록 조회 (미지정 시 전체 조회)")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementUnitResponse.class)))
    public ResponseEntity<SuccessResponse<?>> getUnits(
            @RequestParam(required = false) CalculationType calculationType) {
        List<SettlementUnitResponse> response = settlementService.getUnitsByCalculationType(calculationType);
        return SuccessResponse.ok(response);
    }

    @GetMapping("/items")
    @Operation(summary = "정산 아이템 목록 조회", description = "CalculationType에 따른 정산 아이템 + 단위 목록 조회 (미지정 시 전체 조회)")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementItemResponse.class)))
    public ResponseEntity<SuccessResponse<?>> getItems(
            @RequestParam(required = false) CalculationType calculationType) {
        List<SettlementItemResponse> response = settlementService.getItemsByCalculationType(calculationType);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/structure")
    @Operation(summary = "정산서 구조 수정", description = "수정될 최종 버전을 요청(id null이면 생성, 미포함 시 삭제됨)")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementStructureDto.class)))
    public ResponseEntity<SuccessResponse<?>> updateSettlementStructure(@RequestBody SettlementStructureDto request) {
        SettlementStructureDto response = settlementService.updateSettlementStructure(request);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    @Operation(summary = "택배표 업로드", description = "Excel 파일을 업로드하여 정산 데이터 생성")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeliverySheetUploadResponse.class)))
    public ResponseEntity<SuccessResponse<?>> uploadDeliverySheet(
            @RequestParam("file") MultipartFile file,
            @RequestParam int year,
            @RequestParam int month) {
        DeliverySheetUploadResponse response = deliverySheetService.uploadDeliverySheet(file, year, month);
        return SuccessResponse.ok(response);
    }

    @GetMapping("/detail")
    @Operation(summary = "정산서 조회 (브랜드별)", description = "특정 브랜드의 월별 정산서 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementResponse.class)))
    public ResponseEntity<SuccessResponse<?>> getSettlement(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long brandId) {
        SettlementResponse response = settlementManagementService.getSettlementByBrand(year, month, brandId);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    @Operation(summary = "정산서 목록 조회", description = "특정 월의 모든 정산서 목록 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementResponse.class)))
    public ResponseEntity<SuccessResponse<?>> getSettlements(
            @RequestParam int year,
            @RequestParam int month) {
        List<SettlementResponse> response = settlementManagementService.getSettlementsByYearAndMonth(year, month);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/details/{detailId}")
    @Operation(summary = "정산서 상세 수정", description = "정산서 상세 항목 수정 (금액, 비고 등)")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementDetailResponse.class)))
    public ResponseEntity<SuccessResponse<?>> updateDetail(
            @PathVariable Long detailId,
            @RequestBody SettlementDetailUpdateRequest request) {
        SettlementDetailResponse response = settlementManagementService.updateSettlementDetail(detailId, request);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{settlementId}/confirm")
    @Operation(summary = "정산서 확정", description = "정산서를 확정 상태로 변경")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementResponse.class)))
    public ResponseEntity<SuccessResponse<?>> confirmSettlement(
            @PathVariable Long settlementId,
            @Auth AuthDto auth) {
        Member member = memberRepository.findById(auth.memberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        SettlementResponse response = settlementManagementService.confirmSettlement(settlementId, member);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{settlementId}/revert")
    @Operation(summary = "확정 취소", description = "확정된 정산서를 임시저장 상태로 되돌림")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementResponse.class)))
    public ResponseEntity<SuccessResponse<?>> revertToDraft(@PathVariable Long settlementId) {
        SettlementResponse response = settlementManagementService.revertToDraft(settlementId);
        return SuccessResponse.ok(response);
    }

    @GetMapping("/invoice")
    @Operation(summary = "청구서 조회 (업체별)", description = "특정 업체의 월별 청구서 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvoiceResponse.class)))
    public ResponseEntity<SuccessResponse<?>> getInvoice(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long companyId) {
        InvoiceResponse response = settlementManagementService.getInvoiceByCompany(year, month, companyId);
        return SuccessResponse.ok(response);
    }
}
