package kr.co.ksgk.ims.domain.product.dto.response;

import kr.co.ksgk.ims.domain.brand.dto.response.BrandResponse;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.entity.StorageType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String note,
        BrandResponse brand,
        StorageType storageType,
        BigDecimal cbm,
        BigDecimal storagePricePerCbm,
        Integer quantityPerPallet,
        BigDecimal storagePricePerPallet,
        Long sizeUnitId,
        String sizeUnitName
) {
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .note(product.getNote())
                .brand(BrandResponse.from(product.getBrand()))
                .storageType(product.getStorageType())
                .cbm(product.getCbm())
                .storagePricePerCbm(product.getStoragePricePerCbm())
                .quantityPerPallet(product.getQuantityPerPallet())
                .storagePricePerPallet(product.getStoragePricePerPallet())
                .sizeUnitId(product.getSizeUnit() != null ? product.getSizeUnit().getId() : null)
                .sizeUnitName(product.getSizeUnit() != null ? product.getSizeUnit().getName() : null)
                .build();
    }
}