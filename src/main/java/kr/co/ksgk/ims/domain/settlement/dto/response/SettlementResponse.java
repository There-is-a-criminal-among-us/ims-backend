package kr.co.ksgk.ims.domain.settlement.dto.response;

import kr.co.ksgk.ims.domain.settlement.entity.CalculationType;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementStatus;

import java.time.LocalDateTime;
import java.util.List;

public record SettlementResponse(
        Long id,
        Integer year,
        Integer month,
        Long companyId,
        String companyName,
        SettlementStatus status,
        LocalDateTime confirmedAt,
        String confirmedByName,
        List<ProductColumn> products,
        List<TypeRow> types,
        Long totalAmount
) {
    public record ProductColumn(Long id, String name) {}

    public record ProductCell(Long productId, Long detailId,
                              Integer quantity, Integer unitPrice, Long amount, String note) {}

    public record ItemRow(Long itemId, String itemName,
                          CalculationType calculationType,
                          List<UnitInfo> units,
                          List<ProductCell> cells,
                          Integer totalQuantity, Long totalAmount) {}

    public record UnitInfo(Long id, String name, Integer price) {}

    public record CategoryRow(Long categoryId, String categoryName,
                               List<ItemRow> items) {}

    public record TypeRow(Long typeId, String typeName,
                          List<CategoryRow> categories,
                          List<ItemRow> directItems,
                          Long subtotalAmount) {}
}
