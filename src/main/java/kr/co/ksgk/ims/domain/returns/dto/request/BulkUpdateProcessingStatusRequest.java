package kr.co.ksgk.ims.domain.returns.dto.request;

import kr.co.ksgk.ims.domain.returns.entity.ProcessingStatus;

import java.util.List;

public record BulkUpdateProcessingStatusRequest(
        List<Long> returnIds,
        ProcessingStatus processingStatus
) {
}
