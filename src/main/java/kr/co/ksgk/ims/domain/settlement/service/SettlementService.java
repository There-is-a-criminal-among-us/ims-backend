package kr.co.ksgk.ims.domain.settlement.service;

import kr.co.ksgk.ims.domain.settlement.dto.*;
import kr.co.ksgk.ims.domain.settlement.dto.response.SettlementItemResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.SettlementUnitResponse;
import kr.co.ksgk.ims.domain.settlement.entity.*;
import kr.co.ksgk.ims.domain.settlement.repository.ChargeCategoryRepository;
import kr.co.ksgk.ims.domain.settlement.repository.SettlementItemRepository;
import kr.co.ksgk.ims.domain.settlement.repository.SettlementTypeRepository;
import kr.co.ksgk.ims.domain.settlement.repository.SettlementUnitRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementTypeRepository settlementTypeRepository;
    private final ChargeCategoryRepository chargeCategoryRepository;
    private final SettlementUnitRepository settlementUnitRepository;
    private final SettlementItemRepository settlementItemRepository;

    public List<ChargeCategoryDto> getChargeCategories() {
        return chargeCategoryRepository.findAll().stream()
                .map(ChargeCategoryDto::from)
                .toList();
    }

    public SettlementStructureDto getSettlementStructure() {
        List<SettlementType> types = settlementTypeRepository.findAll();
        List<ChargeCategory> chargeCategories = chargeCategoryRepository.findAll();
        return SettlementStructureDto.from(types, chargeCategories);
    }

    public List<SettlementUnitResponse> getUnitsByCalculationType(CalculationType calculationType) {
        if (calculationType == null) {
            return settlementUnitRepository.findAllWithItem().stream()
                    .map(SettlementUnitResponse::from)
                    .toList();
        }
        return settlementUnitRepository.findByCalculationType(calculationType).stream()
                .map(SettlementUnitResponse::from)
                .toList();
    }

    public List<SettlementItemResponse> getItemsByCalculationType(CalculationType calculationType) {
        if (calculationType == null) {
            return settlementItemRepository.findAllWithUnits().stream()
                    .map(SettlementItemResponse::from)
                    .toList();
        }
        return settlementItemRepository.findByCalculationTypeWithUnits(calculationType).stream()
                .map(SettlementItemResponse::from)
                .toList();
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
                // Update the existing type (fetch all nested entities)
                type = settlementTypeRepository.findByIdWithAll(typeDto.id())
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
        if (categoryDtos == null || categoryDtos.isEmpty()) {
            type.updateCategories(new HashSet<>());
            return;
        }

        Set<SettlementCategory> categories = new HashSet<>();

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
        if (itemDtos == null || itemDtos.isEmpty()) {
            category.updateItems(new HashSet<>());
            return;
        }

        Set<SettlementItem> items = new HashSet<>();

        for (SettlementItemDto itemDto : itemDtos) {
            SettlementItem item;
            CalculationType calcType = itemDto.calculationType() != null ? itemDto.calculationType() : CalculationType.MANUAL;

            if (itemDto.id() == null) {
                // Create a new item
                item = SettlementItem.createForCategory(itemDto.name(), itemDto.displayOrder(), calcType, category);
            } else {
                // Find existing item - first try in category, then in repository (for moving items)
                item = category.getItems().stream()
                        .filter(i -> i.getId().equals(itemDto.id()))
                        .findFirst()
                        .orElseGet(() -> settlementItemRepository.findById(itemDto.id())
                                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_ITEM_NOT_FOUND)));
                // Move item to this category (handles case where item was in type.directItems or another category)
                item.moveToCategory(category);
                item.update(itemDto.name(), itemDto.displayOrder(), calcType);
            }

            // Update units
            updateUnits(item, itemDto.units());
            items.add(item);
        }

        category.updateItems(items);
    }

    private void updateDirectItems(SettlementType type, List<SettlementItemDto> itemDtos) {
        if (itemDtos == null || itemDtos.isEmpty()) {
            type.updateDirectItems(new HashSet<>());
            return;
        }

        Set<SettlementItem> items = new HashSet<>();

        for (SettlementItemDto itemDto : itemDtos) {
            SettlementItem item;
            CalculationType calcType = itemDto.calculationType() != null ? itemDto.calculationType() : CalculationType.MANUAL;

            if (itemDto.id() == null) {
                // Create a new item
                item = SettlementItem.createForType(itemDto.name(), itemDto.displayOrder(), calcType, type);
            } else {
                // Find existing item - first try in type.directItems, then in repository (for moving items)
                item = type.getDirectItems().stream()
                        .filter(i -> i.getId().equals(itemDto.id()))
                        .findFirst()
                        .orElseGet(() -> settlementItemRepository.findById(itemDto.id())
                                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_ITEM_NOT_FOUND)));
                // Move item to this type (handles case where item was in a category)
                item.moveToType(type);
                item.update(itemDto.name(), itemDto.displayOrder(), calcType);
            }

            // Update units
            updateUnits(item, itemDto.units());
            items.add(item);
        }

        type.updateDirectItems(items);
    }

    private void updateUnits(SettlementItem item, List<SettlementUnitDto> unitDtos) {
        if (unitDtos == null || unitDtos.isEmpty()) {
            item.updateUnits(new HashSet<>());
            return;
        }

        Set<SettlementUnit> units = new HashSet<>();

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
