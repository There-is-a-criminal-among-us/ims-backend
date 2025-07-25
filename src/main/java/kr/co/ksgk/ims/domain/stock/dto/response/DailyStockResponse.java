package kr.co.ksgk.ims.domain.stock.dto.response;

import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record DailyStockResponse(
        LocalDate startDate,
        List<DailyStockDetailsDto> stocks
) {
    public static DailyStockResponse of(LocalDate startDate, List<DailyStock> dailyStocks) {
        return DailyStockResponse.builder()
                .startDate(startDate)
                .stocks(dailyStocks.stream()
                        .map(DailyStockDetailsDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}