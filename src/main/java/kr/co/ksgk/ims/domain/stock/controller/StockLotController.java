package kr.co.ksgk.ims.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.stock.dto.response.StockLotResponse;
import kr.co.ksgk.ims.domain.stock.entity.StockLot;
import kr.co.ksgk.ims.domain.stock.service.StockLotService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock-lots")
@Tag(name = "Stock Lot", description = "입고 로트 관리 API")
public class StockLotController {

    private final StockLotService stockLotService;
    private final ProductRepository productRepository;

    @GetMapping("/product/{productId}")
    @Operation(summary = "상품별 로트 현황", description = "특정 상품의 모든 로트 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = StockLotResponse.class)))
    public ResponseEntity<SuccessResponse<?>> getLotsByProduct(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        List<StockLotResponse> response = stockLotService.getLotsByProduct(product).stream()
                .map(StockLotResponse::from)
                .collect(Collectors.toList());

        return SuccessResponse.ok(response);
    }

    @GetMapping("/product/{productId}/remaining")
    @Operation(summary = "상품별 잔여 로트", description = "특정 상품의 잔여 수량이 있는 로트만 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = StockLotResponse.class)))
    public ResponseEntity<SuccessResponse<?>> getLotsWithRemainingByProduct(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        List<StockLotResponse> response = stockLotService.getLotsWithRemainingByProduct(product).stream()
                .map(StockLotResponse::from)
                .collect(Collectors.toList());

        return SuccessResponse.ok(response);
    }

    @GetMapping("/{lotId}")
    @Operation(summary = "로트 상세 조회", description = "특정 로트의 상세 정보 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = StockLotResponse.class)))
    public ResponseEntity<SuccessResponse<?>> getLotById(@PathVariable Long lotId) {
        StockLot stockLot = stockLotService.getLotById(lotId);
        if (stockLot == null) {
            throw new EntityNotFoundException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        return SuccessResponse.ok(StockLotResponse.from(stockLot));
    }

    @GetMapping("/product/{productId}/total")
    @Operation(summary = "상품별 총 잔여 수량", description = "특정 상품의 로트 기반 총 잔여 수량 조회")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<SuccessResponse<?>> getTotalRemainingByProduct(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        int totalRemaining = stockLotService.getTotalRemainingByProduct(product);

        return SuccessResponse.ok(totalRemaining);
    }
}
