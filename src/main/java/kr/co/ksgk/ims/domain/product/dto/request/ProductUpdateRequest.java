package kr.co.ksgk.ims.domain.product.dto.request;

import kr.co.ksgk.ims.domain.product.entity.StorageType;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        String name,
        String note,
        StorageType storageType,
        BigDecimal cbm,
        BigDecimal storagePricePerCbm,
        Integer quantityPerPallet,
        BigDecimal storagePricePerPallet
) {
}
