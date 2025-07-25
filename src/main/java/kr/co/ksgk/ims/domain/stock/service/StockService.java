package kr.co.ksgk.ims.domain.stock.service;

import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.stock.dto.response.DailyStockResponse;
import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import kr.co.ksgk.ims.domain.stock.repository.StockRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public DailyStockResponse getDailyStock(Long memberId, Integer yearParam, Integer monthParam) {
        LocalDate today = LocalDate.now();
        int year = yearParam != null ? yearParam : today.getYear();
        int month = monthParam != null ? monthParam : today.getMonthValue();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = today.minusDays(1);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        List<Long> companyIds = member.getMemberCompanies().stream()
                .map(mc -> mc.getCompany().getId())
                .toList();
        List<Long> brandIds = member.getMemberBrands().stream()
                .map(mb -> mb.getBrand().getId())
                .toList();
        List<Product> products = productRepository.findByCompanyIdInOrBrandIdIn(companyIds, brandIds);
        List<DailyStock> dailyStocks = stockRepository.findAllByProductsAndDateBetween(products, startDate, endDate);
        return DailyStockResponse.of(startDate, dailyStocks);
    }
}
