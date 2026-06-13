package kr.co.ksgk.ims.domain.payslip.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.payslip.dto.request.CreatePayslipRequest;
import kr.co.ksgk.ims.domain.payslip.dto.request.UpdatePayslipRequest;
import kr.co.ksgk.ims.domain.payslip.dto.response.PayslipListResponse;
import kr.co.ksgk.ims.domain.payslip.dto.response.PayslipResponse;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payslip", description = "급여 명세서 API")
public interface PayslipApi {

    @Operation(
            summary = "급여 명세서 등록",
            description = "급여 명세서를 등록합니다. ADMIN, MANAGER 권한이 필요합니다.\n\n" +
                    "S3 업로드 순서:\n" +
                    "1. `POST /api/s3/presigned/upload` 로 presigned URL + s3Key 발급\n" +
                    "2. 발급된 URL로 PDF 파일 직접 업로드 (PUT)\n" +
                    "3. 이 API에 s3Key 포함하여 명세서 등록"
    )
    @ApiResponse(responseCode = "201", description = "등록 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PayslipResponse.class)))
    ResponseEntity<SuccessResponse<?>> createPayslip(@RequestBody CreatePayslipRequest request);

    @Operation(
            summary = "급여 명세서 수정",
            description = "급여 명세서를 수정합니다. ADMIN, MANAGER 권한이 필요합니다.\n\n" +
                    "date는 수정 불가합니다. memberId(오등록 수정)와 s3Key(파일 교체)만 변경 가능합니다."
    )
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PayslipResponse.class)))
    ResponseEntity<SuccessResponse<?>> updatePayslip(@PathVariable Long payslipId,
                                                     @RequestBody UpdatePayslipRequest request);

    @Operation(
            summary = "멤버 급여 명세서 목록 조회",
            description = "특정 멤버의 전체 급여 명세서 목록을 조회합니다.\n\n" +
                    "ADMIN/MANAGER는 모든 멤버 조회 가능, PART_TIME은 본인 것만 조회 가능합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PayslipListResponse.class)))
    ResponseEntity<SuccessResponse<?>> getPayslipsByMember(@Auth Long requesterId,
                                                           @PathVariable Long memberId,
                                                           Authentication authentication);

    @Operation(
            summary = "특정 월 급여 명세서 조회",
            description = "특정 멤버의 특정 월 급여 명세서를 조회합니다.\n\n" +
                    "date 형식: `yyyy-MM` (예: `2025-06`)\n\n" +
                    "ADMIN/MANAGER는 모든 멤버 조회 가능, PART_TIME은 본인 것만 조회 가능합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PayslipResponse.class)))
    ResponseEntity<SuccessResponse<?>> getPayslip(@Auth Long requesterId,
                                                  @PathVariable Long memberId,
                                                  @PathVariable String date,
                                                  Authentication authentication);

    @Operation(
            summary = "급여 명세서 삭제",
            description = "급여 명세서를 삭제합니다. S3 파일도 함께 삭제됩니다.\n\nADMIN, MANAGER 권한이 필요합니다."
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    ResponseEntity<SuccessResponse<?>> deletePayslip(@PathVariable Long payslipId);
}
