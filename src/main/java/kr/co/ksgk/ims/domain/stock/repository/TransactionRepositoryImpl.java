package kr.co.ksgk.ims.domain.stock.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.ksgk.ims.domain.brand.entity.QBrand;
import kr.co.ksgk.ims.domain.company.entity.QCompany;
import kr.co.ksgk.ims.domain.product.entity.QProduct;
import kr.co.ksgk.ims.domain.stock.entity.*;
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
public class TransactionRepositoryImpl implements TransactionCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Transaction> searchTransactions(String search, TransactionGroup type, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        QTransaction transaction = QTransaction.transaction;
        QTransactionType transactionType = QTransactionType.transactionType;
        QProduct product = QProduct.product;
        QBrand brand = QBrand.brand;
        QCompany company = QCompany.company;

        BooleanBuilder builder = new BooleanBuilder();

        if (type != null) builder.and(transaction.transactionType.groupType.eq(type));
        if (startDate != null) builder.and(transaction.createdAt.goe(startDate.atStartOfDay()));
        if (endDate != null) builder.and(transaction.createdAt.loe(endDate.atTime(23, 59, 59)));
        if (search != null && !search.isBlank()) {
            builder.and(
                    product.name.containsIgnoreCase(search)
                            .or(brand.name.containsIgnoreCase(search))
                            .or(company.name.containsIgnoreCase(search))
            );
        }

        List<Transaction> result = queryFactory
                .selectFrom(transaction)
                .leftJoin(transaction.transactionType, transactionType).fetchJoin()
                .leftJoin(transaction.product, product).fetchJoin()
                .leftJoin(product.brand, brand).fetchJoin()
                .leftJoin(brand.company, company).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(transaction.createdAt.desc())
                .fetch();

        long count = Optional.ofNullable(
                queryFactory
                        .select(transaction.count())
                        .from(transaction)
                        .leftJoin(transaction.transactionType, transactionType)
                        .leftJoin(transaction.product, product)
                        .leftJoin(product.brand, brand)
                        .leftJoin(brand.company, company)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(result, pageable, count);
    }
}
