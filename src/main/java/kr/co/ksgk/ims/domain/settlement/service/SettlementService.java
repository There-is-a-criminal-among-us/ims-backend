package kr.co.ksgk.ims.domain.settlement.service;

import kr.co.ksgk.ims.domain.settlement.dto.*;
import kr.co.ksgk.ims.domain.settlement.entity.*;
import kr.co.ksgk.ims.domain.settlement.repository.ChargeCategoryRepository;
import kr.co.ksgk.ims.domain.settlement.repository.SettlementTypeRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementTypeRepository settlementTypeRepository;
    private final ChargeCategoryRepository chargeCategoryRepository;

    public SettlementStructureDto getSettlementStructure() {
        List<SettlementType> types = settlementTypeRepository.findAll();
        List<ChargeCategory> chargeCategories = chargeCategoryRepository.findAll();
        return SettlementStructureDto.from(types, chargeCategories);
    }

    @Transactional
    public SettlementStructureDto updateSettlementStructure(SettlementStructureDto request) {
        updateSettlementTypes(request.types());
        updateChargeCategories(request.chargeCategories());
        return getSettlementStructure();
    }

    private void updateSettlementTypes(List<SettlementTypeDto> typeDtos) {
        List<SettlementType> existingTypes = settlementTypeRepository.findAll();

        // Update or create types
        for (SettlementTypeDto typeDto : typeDtos) {
            SettlementType type;

            if (typeDto.id() == null) {
                // Create a new type
                type = SettlementType.create(typeDto.name(), typeDto.displayOrder());
                settlementTypeRepository.save(type);
            } else {
                // Update the existing type
                type = settlementTypeRepository.findById(typeDto.id())
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_TYPE_NOT_FOUND));
                type.update(typeDto.name(), typeDto.displayOrder());
            }

            // Update categories
            updateCategories(type, typeDto.categories());

            // Update direct items
            updateDirectItems(type, typeDto.items());
        }

        // Delete types not in request
        List<Long> requestTypeIds = typeDtos.stream()
                .map(SettlementTypeDto::id)
                .filter(Objects::nonNull)
                .toList();

        existingTypes.stream()
                .filter(type -> !requestTypeIds.contains(type.getId()))
                .forEach(settlementTypeRepository::delete);
    }

    private void updateCategories(SettlementType type, List<SettlementCategoryDto> categoryDtos) {
        List<SettlementCategory> categories = new ArrayList<>();

        for (SettlementCategoryDto categoryDto : categoryDtos) {
            SettlementCategory category;

            if (categoryDto.id() == null) {
                // Create a new category
                category = SettlementCategory.create(categoryDto.name(), categoryDto.displayOrder(), type);
            } else {
                // Update the existing category
                category = type.getCategories().stream()
                        .filter(c -> c.getId().equals(categoryDto.id()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_CATEGORY_NOT_FOUND));
                category.update(categoryDto.name(), categoryDto.displayOrder());
            }

            // Update items in a category
            updateCategoryItems(category, categoryDto.items());
            categories.add(category);
        }

        type.updateCategories(categories);
    }

    private void updateCategoryItems(SettlementCategory category, List<SettlementItemDto> itemDtos) {
        List<SettlementItem> items = new ArrayList<>();

        for (SettlementItemDto itemDto : itemDtos) {
            SettlementItem item;
            CalculationType calcType = itemDto.calculationType() != null ? itemDto.calculationType() : CalculationType.MANUAL;

            if (itemDto.id() == null) {
                // Create a new item
                item = SettlementItem.createForCategory(itemDto.name(), itemDto.displayOrder(), calcType, category);
            } else {
                // Update the existing item
                item = category.getItems().stream()
                        .filter(i -> i.getId().equals(itemDto.id()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_ITEM_NOT_FOUND));
                item.update(itemDto.name(), itemDto.displayOrder(), calcType);
            }

            // Update units
            updateUnits(item, itemDto.units());
            items.add(item);
        }

        category.updateItems(items);
    }

    private void updateDirectItems(SettlementType type, List<SettlementItemDto> itemDtos) {
        List<SettlementItem> items = new ArrayList<>();

        for (SettlementItemDto itemDto : itemDtos) {
            SettlementItem item;
            CalculationType calcType = itemDto.calculationType() != null ? itemDto.calculationType() : CalculationType.MANUAL;

            if (itemDto.id() == null) {
                // Create a new item
                item = SettlementItem.createForType(itemDto.name(), itemDto.displayOrder(), calcType, type);
            } else {
                // Update existing item
                item = type.getDirectItems().stream()
                        .filter(i -> i.getId().equals(itemDto.id()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_ITEM_NOT_FOUND));
                item.update(itemDto.name(), itemDto.displayOrder(), calcType);
            }

            // Update units
            updateUnits(item, itemDto.units());
            items.add(item);
        }

        type.updateDirectItems(items);
    }

    private void updateUnits(SettlementItem item, List<SettlementUnitDto> unitDtos) {
        List<SettlementUnit> units = new ArrayList<>();

        for (SettlementUnitDto unitDto : unitDtos) {
            SettlementUnit unit;

            if (unitDto.id() == null) {
                // Create a new unit
                unit = SettlementUnit.create(unitDto.name(), unitDto.price(), unitDto.displayOrder(), item);
            } else {
                // Update existing unit
                unit = item.getUnits().stream()
                        .filter(u -> u.getId().equals(unitDto.id()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_UNIT_NOT_FOUND));
                unit.update(unitDto.name(), unitDto.price(), unitDto.displayOrder());
            }

            units.add(unit);
        }

        item.updateUnits(units);
    }

    private void updateChargeCategories(List<ChargeCategoryDto> chargeCategoryDtos) {
        List<ChargeCategory> existingCategories = chargeCategoryRepository.findAll();

        // Update or create charge categories
        for (ChargeCategoryDto categoryDto : chargeCategoryDtos) {
            if (categoryDto.id() == null) {
                // Create a new category
                ChargeCategory category = ChargeCategory.create(categoryDto.name(), categoryDto.displayOrder());
                chargeCategoryRepository.save(category);
            } else {
                // Update the existing category
                ChargeCategory category = chargeCategoryRepository.findById(categoryDto.id())
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CHARGE_CATEGORY_NOT_FOUND));
                category.update(categoryDto.name(), categoryDto.displayOrder());
            }
        }

        // Delete categories not in request
        List<Long> requestCategoryIds = chargeCategoryDtos.stream()
                .map(ChargeCategoryDto::id)
                .filter(Objects::nonNull)
                .toList();

        existingCategories.stream()
                .filter(category -> !requestCategoryIds.contains(category.getId()))
                .forEach(chargeCategoryRepository::delete);
    }
}
