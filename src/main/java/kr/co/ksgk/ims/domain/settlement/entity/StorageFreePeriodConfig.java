package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_storage_free_period_company_product",
                columnNames = {"company_id", "product_id"}
        ),
        indexes = {
                @Index(name = "idx_storage_free_period_company", columnList = "company_id"),
                @Index(name = "idx_storage_free_period_active", columnList = "company_id, is_active")
        }
)
public class StorageFreePeriodConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // null이면 업체 기본값

    @Column(nullable = false)
    private Integer freePeriodDays;

    private LocalDate effectiveFrom;

    private LocalDate effectiveUntil;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Builder
    public StorageFreePeriodConfig(Company company, Product product, Integer freePeriodDays,
                                    LocalDate effectiveFrom, LocalDate effectiveUntil, Boolean isActive) {
        this.company = company;
        this.product = product;
        this.freePeriodDays = freePeriodDays;
        this.effectiveFrom = effectiveFrom;
        this.effectiveUntil = effectiveUntil;
        this.isActive = isActive != null ? isActive : true;
    }

    public static StorageFreePeriodConfig createCompanyDefault(Company company, Integer freePeriodDays) {
        return StorageFreePeriodConfig.builder()
                .company(company)
                .product(null)
                .freePeriodDays(freePeriodDays)
                .isActive(true)
                .build();
    }

    public static StorageFreePeriodConfig createForProduct(Company company, Product product, Integer freePeriodDays) {
        return StorageFreePeriodConfig.builder()
                .company(company)
                .product(product)
                .freePeriodDays(freePeriodDays)
                .isActive(true)
                .build();
    }

    public void updateFreePeriodDays(Integer freePeriodDays) {
        this.freePeriodDays = freePeriodDays;
    }

    public void updateEffectivePeriod(LocalDate effectiveFrom, LocalDate effectiveUntil) {
        this.effectiveFrom = effectiveFrom;
        this.effectiveUntil = effectiveUntil;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 특정 날짜에 유효한지 확인
     */
    public boolean isEffectiveOn(LocalDate date) {
        if (!this.isActive) {
            return false;
        }
        if (this.effectiveFrom != null && date.isBefore(this.effectiveFrom)) {
            return false;
        }
        if (this.effectiveUntil != null && date.isAfter(this.effectiveUntil)) {
            return false;
        }
        return true;
    }

    /**
     * 상품별 설정인지 확인
     */
    public boolean isProductSpecific() {
        return this.product != null;
    }
}
