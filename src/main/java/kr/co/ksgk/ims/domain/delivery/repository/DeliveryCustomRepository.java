package kr.co.ksgk.ims.domain.delivery.repository;

import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Set;

public interface DeliveryCustomRepository {

    Page<Delivery> searchDeliveries(String search, LocalDate startDate, LocalDate endDate,
            Set<Long> memberBrandIds, Set<Long> memberCompanyIds, Pageable pageable);
}
