package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.company.entity.Company;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"company_id", "settlement_item_id"})
})
public class CompanyItemChargeMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_item_id", nullable = false)
    private SettlementItem settlementItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_category_id", nullable = false)
    private ChargeCategory chargeCategory;

    public static CompanyItemChargeMapping create(Company company, SettlementItem settlementItem, ChargeCategory chargeCategory) {
        CompanyItemChargeMapping mapping = new CompanyItemChargeMapping();
        mapping.company = company;
        mapping.settlementItem = settlementItem;
        mapping.chargeCategory = chargeCategory;
        return mapping;
    }

    public void updateChargeCategory(ChargeCategory chargeCategory) {
        this.chargeCategory = chargeCategory;
    }
}