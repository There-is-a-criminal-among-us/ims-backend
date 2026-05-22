package kr.co.ksgk.ims.domain.settlement.service;

import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.settlement.dto.CompanyChargeMappingDto;
import kr.co.ksgk.ims.domain.settlement.dto.ItemChargeMappingDto;
import kr.co.ksgk.ims.domain.settlement.dto.request.UpdateCompanyChargeMappingRequest;
import kr.co.ksgk.ims.domain.settlement.entity.ChargeCategory;
import kr.co.ksgk.ims.domain.settlement.entity.CompanyItemChargeMapping;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementItem;
import kr.co.ksgk.ims.domain.settlement.repository.ChargeCategoryRepository;
import kr.co.ksgk.ims.domain.settlement.repository.CompanyItemChargeMappingRepository;
import kr.co.ksgk.ims.domain.settlement.repository.SettlementItemRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanySettlementService {

    private final CompanyRepository companyRepository;
    private final CompanyItemChargeMappingRepository mappingRepository;
    private final SettlementItemRepository settlementItemRepository;
    private final ChargeCategoryRepository chargeCategoryRepository;

    public CompanyChargeMappingDto getCompanyChargeMapping(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));

        List<SettlementItem> allItems = settlementItemRepository.findAll();
        Map<Long, CompanyItemChargeMapping> mappingByItemId = mappingRepository.findByCompanyId(companyId)
                .stream()
                .collect(Collectors.toMap(m -> m.getSettlementItem().getId(), m -> m));

        List<ItemChargeMappingDto> mappings = allItems.stream()
                .map(item -> mappingByItemId.containsKey(item.getId())
                        ? ItemChargeMappingDto.from(mappingByItemId.get(item.getId()))
                        : ItemChargeMappingDto.unmapped(item))
                .toList();

        return CompanyChargeMappingDto.builder()
                .companyId(company.getId())
                .companyName(company.getName())
                .mappings(mappings)
                .build();
    }

    @Transactional
    public CompanyChargeMappingDto updateCompanyChargeMapping(Long companyId, UpdateCompanyChargeMappingRequest request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));

        // Delete existing mappings
        mappingRepository.deleteByCompanyId(companyId);

        // Create new mappings
        for (UpdateCompanyChargeMappingRequest.ItemMappingRequest itemMapping : request.mappings()) {
            SettlementItem item = settlementItemRepository.findById(itemMapping.settlementItemId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_ITEM_NOT_FOUND));

            ChargeCategory chargeCategory = chargeCategoryRepository.findById(itemMapping.chargeCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CHARGE_CATEGORY_NOT_FOUND));

            CompanyItemChargeMapping mapping = CompanyItemChargeMapping.create(company, item, chargeCategory);
            mappingRepository.save(mapping);
        }

        return getCompanyChargeMapping(companyId);
    }
}