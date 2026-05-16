package kr.co.ksgk.ims.domain.settlement.entity;

public enum CalculationType {
    MANUAL,                 // 수동 입력 (입출고 시 건수/비용)
    STORAGE,                // 보관료 (일별 재고 기반 자동 계산)
    SIZE,                   // 사이즈 (출고 택배비, 품목별 설정)
    RETURN_SIZE,            // 반품 사이즈 (반품 택배비, 품목별 설정)
    REMOTE_AREA,            // 도서산간 (추가 배송비)
    DELIVERY_SHEET_QUANTITY // 집하실적 수량 기반 자동 계산 (정산구조 고정 단가 × 택배표 출고 수량 합계)
}
