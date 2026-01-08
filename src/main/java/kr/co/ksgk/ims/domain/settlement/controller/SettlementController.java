package kr.co.ksgk.ims.domain.settlement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.settlement.dto.SettlementStructureDto;
import kr.co.ksgk.ims.domain.settlement.service.SettlementService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settlements")
@Tag(name = "Settlement", description = "정산서 API")
public class SettlementController {

    private final SettlementService settlementService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/structure")
    @Operation(summary = "정산서 구조 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettlementStructureDto.class)))
    public ResponseEntity<SuccessResponse<?>> getSettlementStructure() {
        SettlementStructureDto response = settlementService.getSettlementStructure();
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
}
