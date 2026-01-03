package kr.co.ksgk.ims.domain.delivery.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.ksgk.ims.domain.brand.entity.QBrand;
import kr.co.ksgk.ims.domain.company.entity.QCompany;
import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import kr.co.ksgk.ims.domain.delivery.entity.QDelivery;
import kr.co.ksgk.ims.domain.product.entity.QProduct;
import kr.co.ksgk.ims.domain.product.entity.QProductMapping;
import kr.co.ksgk.ims.domain.product.entity.QRawProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Delivery> searchDeliveries(String search, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        QDelivery delivery = QDelivery.delivery;
        QRawProduct rawProduct = QRawProduct.rawProduct;
        QProduct product = QProduct.product;
        QBrand brand = QBrand.brand;
        QCompany company = QCompany.company;
        QProductMapping productMapping = QProductMapping.productMapping;

        BooleanBuilder builder = new BooleanBuilder();

        if (startDate != null) builder.and(delivery.createdAt.goe(startDate.atStartOfDay()));
        if (endDate != null) builder.and(delivery.createdAt.loe(endDate.atTime(23, 59, 59)));
        if (search != null && !search.isBlank()) {
            builder.and(
                    rawProduct.name.containsIgnoreCase(search)
                            .or(product.name.containsIgnoreCase(search))
                            .or(brand.name.containsIgnoreCase(search))
                            .or(company.name.containsIgnoreCase(search))
            );
        }

        List<Delivery> result = queryFactory
                .selectFrom(delivery)
                .leftJoin(delivery.rawProduct, rawProduct).fetchJoin()
                .leftJoin(rawProduct.productMappings, productMapping).fetchJoin()
                .leftJoin(productMapping.product, product).fetchJoin()
                .leftJoin(product.brand, brand).fetchJoin()
                .leftJoin(product.brand.company, company).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(delivery.createdAt.desc())
                .fetch();

        long count = Optional.ofNullable(
                queryFactory
                        .select(delivery.count())
                        .from(delivery)
                        .leftJoin(delivery.rawProduct, rawProduct)
                        .leftJoin(rawProduct.productMappings, productMapping)
                        .leftJoin(productMapping.product, product)
                        .leftJoin(product.brand, brand)
                        .leftJoin(product.brand.company, company)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(result, pageable, count);
    }
}
