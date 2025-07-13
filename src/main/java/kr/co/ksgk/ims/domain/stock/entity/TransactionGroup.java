package kr.co.ksgk.ims.domain.stock.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionGroup {
    OUTGOING("출고"),
    INCOMING("입고"),
    ADJUSTMENT("조정");

    private final String description;
}
