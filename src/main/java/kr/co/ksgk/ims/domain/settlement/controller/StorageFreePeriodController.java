package kr.co.ksgk.ims.domain.settlement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.ksgk.ims.domain.settlement.dto.request.StorageFreePeriodConfigRequest;
import kr.co.ksgk.ims.domain.settlement.dto.response.StorageFreePeriodConfigResponse;
import kr.co.ksgk.ims.domain.settlement.service.StorageFreePeriodService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/storage-free-period")
@Tag(name = "Storage Free Period", description = "보관료 무료 기간 설정 API")
public class StorageFreePeriodController {

    private final StorageFreePeriodService storageFreePeriodService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "무료 기간 설정 생성/수정", description = "업체+상품별 무료 기간 설정 생성 또는 수정")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = StorageFreePeriodConfigResponse.class)))
    public ResponseEntity<SuccessResponse<?>> createOrUpdateConfig(
            @Valid @RequestBody StorageFreePeriodConfigRequest request) {
        StorageFreePeriodConfigResponse response = storageFreePeriodService.createOrUpdateConfig(request);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/company/{companyId}")
    @Operation(summary = "업체별 설정 조회", description = "특정 업체의 모든 무료 기간 설정 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = StorageFreePeriodConfigResponse.class)))
    public ResponseEntity<SuccessResponse<?>> getConfigsByCompany(@PathVariable Long companyId) {
        List<StorageFreePeriodConfigResponse> response = storageFreePeriodService.getConfigsByCompany(companyId);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{configId}")
    @Operation(summary = "설정 삭제", description = "무료 기간 설정 비활성화")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<SuccessResponse<?>> deleteConfig(@PathVariable Long configId) {
        storageFreePeriodService.deleteConfig(configId);
        return SuccessResponse.ok("무료 기간 설정이 삭제되었습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/company/{companyId}/default")
    @Operation(summary = "업체 기본값 조회", description = "업체의 기본 무료 기간 조회")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<SuccessResponse<?>> getCompanyDefault(@PathVariable Long companyId) {
        Integer freePeriodDays = storageFreePeriodService.getCompanyDefault(companyId);
        return SuccessResponse.ok(freePeriodDays);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/company/{companyId}/default")
    @Operation(summary = "업체 기본값 수정", description = "업체의 기본 무료 기간 수정")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<SuccessResponse<?>> updateCompanyDefault(
            @PathVariable Long companyId,
            @RequestParam Integer freePeriodDays) {
        storageFreePeriodService.updateCompanyDefault(companyId, freePeriodDays);
        return SuccessResponse.ok("업체 기본 무료 기간이 수정되었습니다.");
    }
}
