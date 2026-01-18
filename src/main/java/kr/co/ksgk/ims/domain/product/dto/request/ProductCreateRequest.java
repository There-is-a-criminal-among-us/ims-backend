package kr.co.ksgk.ims.domain.product.dto.request;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.entity.StorageType;

import java.math.BigDecimal;

public record ProductCreateRequest(
        Long brandId,
        String name,
        String note,
        StorageType storageType,
        BigDecimal cbm,
        BigDecimal storagePricePerCbm,
        Integer quantityPerPallet,
        BigDecimal storagePricePerPallet,
        Long sizeUnitId,
        Long returnSizeUnitId,
        String coupangCode
) {
    public Product toEntity(Brand brand) {
        Product product = Product.builder()
                .brand(brand)
                .name(name)
                .note(note)
                .build();

        if (storageType != null) {
            product.updateStorageSettings(
                    storageType,
                    cbm,
                    storagePricePerCbm,
                    quantityPerPallet,
                    storagePricePerPallet
            );
        }

        if (coupangCode != null) {
            product.updateCoupangCode(coupangCode);
        }

        return product;
    }
}
