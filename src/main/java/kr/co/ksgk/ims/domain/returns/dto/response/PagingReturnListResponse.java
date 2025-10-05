package kr.co.ksgk.ims.domain.returns.dto.response;

import kr.co.ksgk.ims.domain.returns.entity.ReturnInfo;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingReturnListResponse(
        PageResponse page,
        List<ReturnListResponse> returns
) {
    public static PagingReturnListResponse of(Page<ReturnInfo> pageReturnInfo, List<ReturnListResponse> returns) {
        return PagingReturnListResponse.builder()
                .page(PageResponse.from(pageReturnInfo))
                .returns(returns)
                .build();
    }
}
