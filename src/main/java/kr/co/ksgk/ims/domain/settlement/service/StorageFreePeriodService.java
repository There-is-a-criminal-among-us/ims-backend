package kr.co.ksgk.ims.domain.settlement.service;

import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.settlement.dto.StorageFreePeriodConfigRequest;
import kr.co.ksgk.ims.domain.settlement.dto.StorageFreePeriodConfigResponse;
import kr.co.ksgk.ims.domain.settlement.entity.StorageFreePeriodConfig;
import kr.co.ksgk.ims.domain.settlement.repository.StorageFreePeriodConfigRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StorageFreePeriodService {

    private static final int SYSTEM_DEFAULT_FREE_PERIOD_DAYS = 0;

    private final StorageFreePeriodConfigRepository configRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;

    /**
     * 무료 기간 조회 (우선순위 적용)
     * 1. 업체+상품 특정 설정
     * 2. 업체 기본 설정 (product_id IS NULL)
     * 3. Company.defaultStorageFreePeriodDays
     * 4. 시스템 기본값 (0일)
     */
    public int getFreePeriodDays(Product product, LocalDate date) {
        Company company = product.getBrand().getCompany();

        // 1. 업체+상품 특정 설정 확인
        Optional<StorageFreePeriodConfig> productConfig =
                configRepository.findEffectiveByCompanyAndProductAndDate(company, product, date);
        if (productConfig.isPresent()) {
            log.debug("무료 기간 설정 (상품별): Company={}, Product={}, Days={}",
                    company.getId(), product.getId(), productConfig.get().getFreePeriodDays());
            return productConfig.get().getFreePeriodDays();
        }

        // 2. 업체 기본 설정 확인
        Optional<StorageFreePeriodConfig> companyConfig =
                configRepository.findEffectiveCompanyDefaultByDate(company, date);
        if (companyConfig.isPresent()) {
            log.debug("무료 기간 설정 (업체 기본): Company={}, Days={}",
                    company.getId(), companyConfig.get().getFreePeriodDays());
            return companyConfig.get().getFreePeriodDays();
        }

        // 3. Company.defaultStorageFreePeriodDays 확인
        if (company.getDefaultStorageFreePeriodDays() != null) {
            log.debug("무료 기간 설정 (Company 필드): Company={}, Days={}",
                    company.getId(), company.getDefaultStorageFreePeriodDays());
            return company.getDefaultStorageFreePeriodDays();
        }

        // 4. 시스템 기본값
        log.debug("무료 기간 설정 (시스템 기본값): Days={}", SYSTEM_DEFAULT_FREE_PERIOD_DAYS);
        return SYSTEM_DEFAULT_FREE_PERIOD_DAYS;
    }

    /**
     * 무료 기간 설정 생성/수정
     */
    @Transactional
    public StorageFreePeriodConfigResponse createOrUpdateConfig(StorageFreePeriodConfigRequest request) {
        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));

        Product product = null;
        if (request.productId() != null) {
            product = productRepository.findById(request.productId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        }

        Optional<StorageFreePeriodConfig> existingConfig =
                configRepository.findByCompanyAndProduct(company, product);

        StorageFreePeriodConfig config;
        if (existingConfig.isPresent()) {
            config = existingConfig.get();
            config.updateFreePeriodDays(request.freePeriodDays());
            if (request.effectiveFrom() != null || request.effectiveUntil() != null) {
                config.updateEffectivePeriod(request.effectiveFrom(), request.effectiveUntil());
            }
            config.activate();
            log.info("무료 기간 설정 수정 - Company: {}, Product: {}, Days: {}",
                    company.getId(), product != null ? product.getId() : "default", request.freePeriodDays());
        } else {
            config = StorageFreePeriodConfig.builder()
                    .company(company)
                    .product(product)
                    .freePeriodDays(request.freePeriodDays())
                    .effectiveFrom(request.effectiveFrom())
                    .effectiveUntil(request.effectiveUntil())
                    .isActive(true)
                    .build();
            configRepository.save(config);
            log.info("무료 기간 설정 생성 - Company: {}, Product: {}, Days: {}",
                    company.getId(), product != null ? product.getId() : "default", request.freePeriodDays());
        }

        return StorageFreePeriodConfigResponse.from(config);
    }

    /**
     * 특정 업체의 모든 설정 조회
     */
    public List<StorageFreePeriodConfigResponse> getConfigsByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));

        return configRepository.findByCompanyAndIsActiveTrueOrderByProductIdAsc(company).stream()
                .map(StorageFreePeriodConfigResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 설정 삭제 (비활성화)
     */
    @Transactional
    public void deleteConfig(Long configId) {
        StorageFreePeriodConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        config.deactivate();
        log.info("무료 기간 설정 비활성화 - ID: {}", configId);
    }

    /**
     * 업체 기본값 수정
     */
    @Transactional
    public void updateCompanyDefault(Long companyId, Integer freePeriodDays) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        company.updateDefaultStorageFreePeriodDays(freePeriodDays);
        log.info("업체 기본 무료 기간 수정 - Company: {}, Days: {}", companyId, freePeriodDays);
    }
}
