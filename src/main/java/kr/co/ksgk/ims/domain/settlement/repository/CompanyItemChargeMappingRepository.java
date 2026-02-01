package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.CompanyItemChargeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyItemChargeMappingRepository extends JpaRepository<CompanyItemChargeMapping, Long> {

    @Query("SELECT m FROM CompanyItemChargeMapping m " +
            "JOIN FETCH m.settlementItem " +
            "JOIN FETCH m.chargeCategory " +
            "WHERE m.company.id = :companyId")
    List<CompanyItemChargeMapping> findByCompanyId(@Param("companyId") Long companyId);

    void deleteByCompanyId(Long companyId);
}
