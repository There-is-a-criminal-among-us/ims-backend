package kr.co.ksgk.ims.domain.brand.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.brand.dto.request.BrandRequest;
import kr.co.ksgk.ims.domain.brand.dto.response.BrandResponse;
import kr.co.ksgk.ims.domain.brand.dto.response.PagingBrandResponse;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Brand", description = "브랜드 관련 API")
public interface BrandApi {

    @Operation(
            summary = "브랜드 등록",
            description = "새로운 브랜드를 등록합니다"
    )
    @ApiResponse(responseCode = "200", description = "브랜드 등록 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BrandResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> createBrand(BrandRequest request);

    @Operation(
            summary = "브랜드 목록 조회",
            description = "브랜드 목록을 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "브랜드 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingBrandResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getAllBrands(String search, int page, int size);

    @Operation(
            summary = "브랜드 조회",
            description = "특정 브랜드의 정보를 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "브랜드 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BrandResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getBrand(Long brandId);

    @Operation(
            summary = "브랜드 수정",
            description = "특정 브랜드의 정보를 수정합니다"
    )
    @ApiResponse(responseCode = "200", description = "브랜드 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BrandResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> updateBrand(Long brandId, BrandRequest request);

    @Operation(
            summary = "브랜드 삭제",
            description = "특정 브랜드를 삭제합니다"
    )

    @ApiResponse(responseCode = "204", description = "브랜드 삭제 성공")
    ResponseEntity<SuccessResponse<?>> deleteBrand(Long brandId);
}
