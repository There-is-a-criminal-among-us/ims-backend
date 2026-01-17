package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.CompanyItemChargeMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyItemChargeMappingRepository extends JpaRepository<CompanyItemChargeMapping, Long> {

    List<CompanyItemChargeMapping> findByCompanyId(Long companyId);

    void deleteByCompanyId(Long companyId);
}
