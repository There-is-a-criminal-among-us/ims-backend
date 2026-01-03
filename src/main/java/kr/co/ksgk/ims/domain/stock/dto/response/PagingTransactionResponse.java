package kr.co.ksgk.ims.domain.stock.dto.response;

import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingTransactionResponse(
        PageResponse page,
        List<TransactionResponse> transactions
) {
    public static PagingTransactionResponse of(Page<Transaction> pageTransaction, List<TransactionResponse> transactions) {
        return PagingTransactionResponse.builder()
                .page(PageResponse.from(pageTransaction))
                .transactions(transactions)
                .build();
    }
}
