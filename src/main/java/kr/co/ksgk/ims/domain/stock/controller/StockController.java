package kr.co.ksgk.ims.domain.stock.controller;

import kr.co.ksgk.ims.domain.stock.dto.response.DailyStockResponse;
import kr.co.ksgk.ims.domain.stock.service.StockService;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StockController implements StockApi {

    private final StockService stockService;

    @GetMapping("/stocks")
    public ResponseEntity<SuccessResponse<?>> getDailyStock(@Auth Long memberId,
                                                            @RequestParam(required = false) Integer year,
                                                            @RequestParam(required = false) Integer month) {
        DailyStockResponse dailyStockResponse = stockService.getDailyStock(memberId, year, month);
        return SuccessResponse.ok(dailyStockResponse);
    }
}
