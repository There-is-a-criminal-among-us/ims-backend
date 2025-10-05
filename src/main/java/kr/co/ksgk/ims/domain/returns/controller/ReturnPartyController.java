package kr.co.ksgk.ims.domain.returns.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.co.ksgk.ims.domain.returns.dto.request.CreateReturnPartyRequest;
import kr.co.ksgk.ims.domain.returns.dto.request.UpdateReturnPartyRequest;
import kr.co.ksgk.ims.domain.returns.dto.response.ReturnPartyResponse;
import kr.co.ksgk.ims.domain.returns.service.ReturnPartyService;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/returns")
public class ReturnPartyController {

    private final ReturnPartyService returnPartyService;

    @Operation(
            summary = "쇼핑몰 등록",
            description = "쇼핑몰을 등록합니다"
    )
    @ApiResponse(responseCode = "201", description = "쇼핑몰 등록 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReturnPartyResponse.class))
    )
    @PostMapping("/mall")
    public ResponseEntity<SuccessResponse<?>> createReturnMall(@Auth Long memberId,
                                                               @RequestBody @Valid CreateReturnPartyRequest request) {
        ReturnPartyResponse response = returnPartyService.createReturnMall(memberId, request);
        return SuccessResponse.created(response);
    }

    @Operation(
            summary = "접수자 등록",
            description = "접수자를 등록합니다"
    )
    @ApiResponse(responseCode = "201", description = "접수자 등록 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReturnPartyResponse.class))
    )
    @PostMapping("/handler")
    public ResponseEntity<SuccessResponse<?>> createReturnHandler(@Auth Long memberId,
                                                                  @RequestBody @Valid CreateReturnPartyRequest request) {
        ReturnPartyResponse response = returnPartyService.createReturnHandler(memberId, request);
        return SuccessResponse.created(response);
    }

    @Operation(
            summary = "쇼핑몰 목록 조회",
            description = "로그인한 사용자가 관리하는 브랜드들의 쇼핑몰 목록을 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "쇼핑몰 목록 조회 성공")
    @GetMapping("/malls")
    public ResponseEntity<SuccessResponse<?>> getReturnMalls(@Auth Long memberId) {
        List<ReturnPartyResponse> responses = returnPartyService.getReturnMallsByMember(memberId);
        return SuccessResponse.ok(responses);
    }

    @Operation(
            summary = "접수자 목록 조회",
            description = "로그인한 사용자가 관리하는 브랜드들의 접수자 목록을 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "접수자 목록 조회 성공")
    @GetMapping("/handlers")
    public ResponseEntity<SuccessResponse<?>> getReturnHandlers(@Auth Long memberId) {
        List<ReturnPartyResponse> responses = returnPartyService.getReturnHandlersByMember(memberId);
        return SuccessResponse.ok(responses);
    }

    @Operation(
            summary = "쇼핑몰 수정",
            description = "쇼핑몰 정보를 수정합니다"
    )
    @ApiResponse(responseCode = "200", description = "쇼핑몰 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReturnPartyResponse.class))
    )
    @PutMapping("/mall/{mallId}")
    public ResponseEntity<SuccessResponse<?>> updateReturnMall(@Auth Long memberId, @PathVariable Long mallId,
                                                               @RequestBody @Valid UpdateReturnPartyRequest request) {
        ReturnPartyResponse response = returnPartyService.updateReturnMall(memberId, mallId, request);
        return SuccessResponse.ok(response);
    }

    @Operation(
            summary = "접수자 수정",
            description = "접수자 정보를 수정합니다"
    )
    @ApiResponse(responseCode = "200", description = "접수자 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReturnPartyResponse.class))
    )
    @PutMapping("/handler/{handlerId}")
    public ResponseEntity<SuccessResponse<?>> updateReturnHandler(@Auth Long memberId, @PathVariable Long handlerId,
                                                                  @RequestBody @Valid UpdateReturnPartyRequest request) {
        ReturnPartyResponse response = returnPartyService.updateReturnHandler(memberId, handlerId, request);
        return SuccessResponse.ok(response);
    }

    @Operation(
            summary = "쇼핑몰 삭제",
            description = "쇼핑몰을 삭제합니다"
    )
    @ApiResponse(responseCode = "204", description = "쇼핑몰 삭제 성공")
    @DeleteMapping("/mall/{mallId}")
    public ResponseEntity<SuccessResponse<?>> deleteReturnMall(@Auth Long memberId, @PathVariable Long mallId) {
        returnPartyService.deleteReturnMall(memberId, mallId);
        return SuccessResponse.noContent();
    }

    @Operation(
            summary = "접수자 삭제",
            description = "접수자를 삭제합니다"
    )
    @ApiResponse(responseCode = "204", description = "접수자 삭제 성공")
    @DeleteMapping("/handler/{handlerId}")
    public ResponseEntity<SuccessResponse<?>> deleteReturnHandler(@Auth Long memberId, @PathVariable Long handlerId) {
        returnPartyService.deleteReturnHandler(memberId, handlerId);
        return SuccessResponse.noContent();
    }
}
