package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.settlement.entity.StorageFreePeriodConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StorageFreePeriodConfigRepository extends JpaRepository<StorageFreePeriodConfig, Long> {

    /**
     * 특정 업체 + 상품 조합의 설정 조회
     */
    Optional<StorageFreePeriodConfig> findByCompanyAndProduct(Company company, Product product);

    /**
     * 특정 업체의 기본 설정 조회 (product가 null인 것)
     */
    @Query("""
            SELECT c FROM StorageFreePeriodConfig c
            WHERE c.company = :company
            AND c.product IS NULL
            AND c.isActive = true
            """)
    Optional<StorageFreePeriodConfig> findCompanyDefault(@Param("company") Company company);

    /**
     * 특정 업체의 모든 설정 조회
     */
    List<StorageFreePeriodConfig> findByCompanyOrderByProductIdAsc(Company company);

    /**
     * 특정 업체의 활성 설정만 조회
     */
    List<StorageFreePeriodConfig> findByCompanyAndIsActiveTrueOrderByProductIdAsc(Company company);

    /**
     * 특정 상품에 대한 활성 설정 조회 (업체+상품 특정 설정)
     */
    @Query("""
            SELECT c FROM StorageFreePeriodConfig c
            WHERE c.company = :company
            AND c.product = :product
            AND c.isActive = true
            """)
    Optional<StorageFreePeriodConfig> findActiveByCompanyAndProduct(
            @Param("company") Company company,
            @Param("product") Product product);

    /**
     * 특정 날짜에 유효한 설정 조회 (상품별)
     */
    @Query("""
            SELECT c FROM StorageFreePeriodConfig c
            WHERE c.company = :company
            AND c.product = :product
            AND c.isActive = true
            AND (c.effectiveFrom IS NULL OR c.effectiveFrom <= :date)
            AND (c.effectiveUntil IS NULL OR c.effectiveUntil >= :date)
            """)
    Optional<StorageFreePeriodConfig> findEffectiveByCompanyAndProductAndDate(
            @Param("company") Company company,
            @Param("product") Product product,
            @Param("date") LocalDate date);

    /**
     * 특정 날짜에 유효한 업체 기본 설정 조회
     */
    @Query("""
            SELECT c FROM StorageFreePeriodConfig c
            WHERE c.company = :company
            AND c.product IS NULL
            AND c.isActive = true
            AND (c.effectiveFrom IS NULL OR c.effectiveFrom <= :date)
            AND (c.effectiveUntil IS NULL OR c.effectiveUntil >= :date)
            """)
    Optional<StorageFreePeriodConfig> findEffectiveCompanyDefaultByDate(
            @Param("company") Company company,
            @Param("date") LocalDate date);

    /**
     * 업체 + 상품 조합이 이미 존재하는지 확인
     */
    boolean existsByCompanyAndProduct(Company company, Product product);
}
