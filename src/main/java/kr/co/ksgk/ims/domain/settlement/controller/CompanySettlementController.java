package kr.co.ksgk.ims.domain.settlement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.settlement.dto.CompanyChargeMappingDto;
import kr.co.ksgk.ims.domain.settlement.dto.request.UpdateCompanyChargeMappingRequest;
import kr.co.ksgk.ims.domain.settlement.service.CompanySettlementService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies/{companyId}/settlement")
@Tag(name = "Company Settlement", description = "업체별 정산 설정 API")
public class CompanySettlementController {

    private final CompanySettlementService companySettlementService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/charge-mapping")
    @Operation(summary = "업체별 항목-결제그룹 매핑 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyChargeMappingDto.class)))
    public ResponseEntity<SuccessResponse<?>> getCompanyChargeMapping(@PathVariable Long companyId) {
        CompanyChargeMappingDto response = companySettlementService.getCompanyChargeMapping(companyId);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/charge-mapping")
    @Operation(summary = "업체별 항목-결제그룹 매핑 수정", description = "전체 매핑을 교체합니다. 모든 Item에 대한 매핑이 필요합니다.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyChargeMappingDto.class)))
    public ResponseEntity<SuccessResponse<?>> updateCompanyChargeMapping(
            @PathVariable Long companyId,
            @RequestBody UpdateCompanyChargeMappingRequest request) {
        CompanyChargeMappingDto response = companySettlementService.updateCompanyChargeMapping(companyId, request);
        return SuccessResponse.ok(response);
    }
}