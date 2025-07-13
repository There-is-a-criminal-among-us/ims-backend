package kr.co.ksgk.ims.domain.stock.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    ADJUSTMENT("조정"),
    PENDING("대기"),
    CONFIRM("확인");

    private final String description;
}
