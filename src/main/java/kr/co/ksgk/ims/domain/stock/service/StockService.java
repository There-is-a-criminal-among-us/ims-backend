package kr.co.ksgk.ims.domain.stock.service;

import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.Role;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.stock.dto.response.DailyStockResponse;
import kr.co.ksgk.ims.domain.stock.dto.response.DailyStockDetailsDto;
import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import kr.co.ksgk.ims.domain.stock.entity.DailyStockCache;
import kr.co.ksgk.ims.domain.stock.repository.StockRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final RealTimeStockService realTimeStockService;

    public DailyStockResponse getDailyStock(Long memberId, Integer yearParam, Integer monthParam) {
        LocalDate today = LocalDate.now();
        int year = yearParam != null ? yearParam : today.getYear();
        int month = monthParam != null ? monthParam : today.getMonthValue();
        LocalDate startDate = LocalDate.of(year, month, 1);
        
        // 현재 월이면 오늘까지, 과거 월이면 월 마지막까지
        LocalDate endDate;
        boolean includeToday = false;
        if (year == today.getYear() && month == today.getMonthValue()) {
            endDate = today.minusDays(1); // 어제까지는 DailyStock에서 조회
            includeToday = true; // 오늘 데이터는 별도로 캐시에서 조회
        } else {
            endDate = YearMonth.of(year, month).atEndOfMonth();
        }
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        List<Long> companyIds = member.getMemberCompanies().stream()
                .map(mc -> mc.getCompany().getId())
                .toList();
        List<Long> brandIds = member.getMemberBrands().stream()
                .map(mb -> mb.getBrand().getId())
                .toList();
        List<Product> products = member.getRole() == Role.ADMIN ?
                productRepository.findAll() :
                productRepository.findByCompanyIdInOrBrandIdIn(companyIds, brandIds);
        
        // 과거 데이터 조회 (DailyStock 테이블에서)
        List<DailyStock> historicalStocks = stockRepository.findAllByProductsAndDateBetween(products, startDate, endDate);
        List<DailyStockDetailsDto> allStockDetails = new ArrayList<>();
        
        // 과거 데이터를 DTO로 변환
        allStockDetails.addAll(historicalStocks.stream()
                .map(DailyStockDetailsDto::from)
                .toList());
        
        // 현재 월이면 오늘 데이터도 추가 (Redis 캐시에서)
        if (includeToday) {
            List<DailyStockCache> todayStocks = realTimeStockService.getTodayStockForProducts(products);
            allStockDetails.addAll(todayStocks.stream()
                    .map(this::convertCacheToDto)
                    .toList());
        }
        
        return DailyStockResponse.builder()
                .startDate(startDate)
                .stocks(allStockDetails)
                .build();
    }
    
    private DailyStockDetailsDto convertCacheToDto(DailyStockCache cache) {
        return DailyStockDetailsDto.builder()
                .productId(cache.getProductId())
                .date(cache.getStockDate())
                .currentStock(cache.getCurrentStock())
                .inboundTotal(cache.getInboundTotal())
                .inboundDetails(DailyStockDetailsDto.InboundDetails.builder()
                        .incoming(cache.getIncoming())
                        .returnIncoming(cache.getReturnIncoming())
                        .build())
                .outboundTotal(cache.getOutboundTotal())
                .outboundDetails(DailyStockDetailsDto.OutboundDetails.builder()
                        .outgoing(cache.getOutgoing())
                        .coupangFulfillment(cache.getCoupangFulfillment())
                        .naverFulfillment(cache.getNaverFulfillment())
                        .deliveryOutgoing(cache.getDeliveryOutgoing())
                        .redelivery(cache.getRedelivery())
                        .build())
                .adjustmentTotal(cache.getAdjustmentTotal())
                .adjustmentDetails(DailyStockDetailsDto.AdjustmentDetails.builder()
                        .damaged(cache.getDamaged())
                        .disposal(cache.getDisposal())
                        .lost(cache.getLost())
                        .adjustment(cache.getAdjustment())
                        .build())
                .build();
    }
}
