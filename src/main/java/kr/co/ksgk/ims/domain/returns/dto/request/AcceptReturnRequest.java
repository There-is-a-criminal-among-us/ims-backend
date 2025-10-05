package kr.co.ksgk.ims.domain.returns.dto.request;

import java.util.List;

public record AcceptReturnRequest(
        List<Long> returnIds
) {
}
