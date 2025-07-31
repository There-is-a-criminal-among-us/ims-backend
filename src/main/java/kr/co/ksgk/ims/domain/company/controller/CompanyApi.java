package kr.co.ksgk.ims.domain.company.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.company.dto.TreeResponse;
import kr.co.ksgk.ims.domain.company.dto.request.CompanyRequest;
import kr.co.ksgk.ims.domain.company.dto.response.CompanyResponse;
import kr.co.ksgk.ims.domain.company.dto.response.PagingCompanyResponse;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Company", description = "사업자 관련 API")
public interface CompanyApi {

    @Operation(
            summary = "품목 트리 조회",
            description = "품목 트리를 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "품목 트리 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TreeResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getCompanyTree();

    @Operation(
            summary = "사업자 등록",
            description = "새로운 사업자를 등록합니다"
    )
    @ApiResponse(responseCode = "201", description = "사업자 등록 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CompanyResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> createCompany(CompanyRequest request);

    @Operation(
            summary = "사업자 목록 조회",
            description = "사업자 목록을 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "사업자 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingCompanyResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getAllCompanies(String search, int page, int size);

    @Operation(
            summary = "사업자 조회",
            description = "특정 사업자의 정보를 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "사업자 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CompanyResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getCompany(Long companyId);

    @Operation(
            summary = "사업자 수정",
            description = "특정 사업자의 정보를 수정합니다"
    )
    @ApiResponse(responseCode = "200", description = "사업자 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CompanyResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> updateCompany(Long companyId, CompanyRequest request);

    @Operation(
            summary = "사업자 삭제",
            description = "특정 사업자를 삭제합니다"
    )
    @ApiResponse(responseCode = "204", description = "사업자 삭제 성공")
    ResponseEntity<SuccessResponse<?>> deleteCompany(Long companyId);
}
