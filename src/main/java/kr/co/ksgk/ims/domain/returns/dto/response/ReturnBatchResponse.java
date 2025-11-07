package kr.co.ksgk.ims.domain.returns.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.ksgk.ims.domain.returns.entity.ReturnInfo;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "회수 정보 일괄 조회 응답")
public record ReturnBatchResponse(
        @Schema(description = "담당자 이름", example = "홍길동")
        String handlerName,

        @Schema(description = "쇼핑몰 이름", example = "쿠팡")
        String mallName,

        @Schema(description = "구매자", example = "김철수")
        String buyer,

        @Schema(description = "연락처", example = "010-1234-5678")
        String phone,

        @Schema(description = "상품명", example = "나이키 운동화")
        String productName,

        @Schema(description = "판매 가능 수량 (회수송장이 없으면 null)", example = "2")
        Integer resalableQuantity,

        @Schema(description = "반품수량", example = "1")
        Integer returnQuantity,

        @Schema(description = "회수송장", example = "1234567890")
        String returnInvoice,

        @Schema(description = "생성일", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt
) {
    public static ReturnBatchResponse from(ReturnInfo returnInfo, Integer resalableQuantity) {
        return ReturnBatchResponse.builder()
                .handlerName(returnInfo.getReturnHandler().getName())
                .mallName(returnInfo.getReturnMall().getName())
                .buyer(returnInfo.getBuyer())
                .phone(returnInfo.getPhone())
                .productName(returnInfo.getProductName())
                .resalableQuantity(resalableQuantity)
                .returnQuantity(returnInfo.getQuantity())
                .returnInvoice(returnInfo.getReturnInvoice())
                .createdAt(returnInfo.getCreatedAt())
                .build();
    }
}
