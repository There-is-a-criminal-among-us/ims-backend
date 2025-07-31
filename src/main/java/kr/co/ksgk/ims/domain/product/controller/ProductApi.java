package kr.co.ksgk.ims.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.product.dto.request.ProductMappingRequest;
import kr.co.ksgk.ims.domain.product.dto.request.ProductRequest;
import kr.co.ksgk.ims.domain.product.dto.response.PagingProductMappingResponse;
import kr.co.ksgk.ims.domain.product.dto.response.PagingProductResponse;
import kr.co.ksgk.ims.domain.product.dto.response.ProductMappingResponse;
import kr.co.ksgk.ims.domain.product.dto.response.ProductResponse;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Product", description = "품목 관련 API")
public interface ProductApi {

    @Operation(
            summary = "품목 등록",
            description = "새로운 품목을 등록합니다"
    )
    @ApiResponse(responseCode = "201", description = "품목 등록 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> createProduct(ProductRequest request);

    @Operation(
            summary = "품목 목록 조회",
            description = "품목 목록을 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "품목 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingProductResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getAllProducts(String search, int page, int size);

    @Operation(
            summary = "품목 조회",
            description = "특정 품목의 정보를 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "품목 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getProduct(Long productId);

    @Operation(
            summary = "품목 수정",
            description = "특정 품목의 정보를 수정합니다"
    )
    @ApiResponse(responseCode = "200", description = "품목 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> updateProduct(Long productId, ProductRequest request);

    @Operation(
            summary = "품목 삭제",
            description = "특정 품목을 삭제합니다"
    )
    @ApiResponse(responseCode = "204", description = "품목 삭제 성공")
    ResponseEntity<SuccessResponse<?>> deleteProduct(Long productId);

    @Operation(
            summary = "품목 매핑 등록",
            description = "새로운 품목 매핑을 등록합니다"
    )
    @ApiResponse(responseCode = "201", description = "품목 매핑 등록 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductMappingResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> createProductMapping(ProductMappingRequest request);

    @Operation(
            summary = "품목 매핑 목록 조회",
            description = "품목 매핑 목록을 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "품목 매핑 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingProductMappingResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getProductMapping(String search, int page, int size);

    @Operation(
            summary = "품목 매핑 삭제",
            description = "특정 품목 매핑을 삭제합니다"
    )
    @ApiResponse(responseCode = "204", description = "품목 매핑 삭제 성공")
    ResponseEntity<SuccessResponse<?>> deleteProductMapping(Long rawProductId);
}
