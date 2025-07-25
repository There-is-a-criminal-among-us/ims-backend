package kr.co.ksgk.ims.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.stock.dto.response.DailyStockResponse;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Stock API", description = "재고 관련 API")
public interface StockApi {

    @Operation(
            summary = "월간 재고 조회",
            description = "특정 연도와 월의 재고를 조회합니다. 연도와 월을 지정하지 않으면 현재 연도와 월의 데이터를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "일별 재고 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DailyStockResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getDailyStock(@Auth Long memberId,
                                                     @RequestParam(required = false) Integer year,
                                                     @RequestParam(required = false) Integer month);
}
