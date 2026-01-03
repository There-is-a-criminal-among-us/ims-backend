package kr.co.ksgk.ims.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.ksgk.ims.domain.stock.dto.response.DailyStockResponse;
import kr.co.ksgk.ims.domain.stock.service.DailyStockScheduler;
import kr.co.ksgk.ims.domain.stock.service.StockService;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StockController implements StockApi {

    private final StockService stockService;
    private final DailyStockScheduler dailyStockScheduler;

    @GetMapping("/stocks")
    public ResponseEntity<SuccessResponse<?>> getDailyStock(@Auth Long memberId,
                                                            @RequestParam(required = false) Integer year,
                                                            @RequestParam(required = false) Integer month) {
        DailyStockResponse dailyStockResponse = stockService.getDailyStock(memberId, year, month);
        return SuccessResponse.ok(dailyStockResponse);
    }

    @PostMapping("/stocks/generate")
    @Operation(summary = "DailyStock 수동 생성", description = "특정 날짜의 DailyStock 데이터를 수동으로 생성합니다. (테스트용)")
    public ResponseEntity<SuccessResponse<?>> generateDailyStock(
            @RequestParam(required = false) String date
    ) {
        try {
            LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now().minusDays(1);

            // 스케줄러 로직을 직접 호출 (테스트용)
            dailyStockScheduler.createDailyStockData();

            return SuccessResponse.ok("DailyStock 데이터 생성 완료: " + targetDate);
        } catch (Exception e) {
            return SuccessResponse.ok("DailyStock 데이터 생성 실패: " + e.getMessage());
        }
    }
}
