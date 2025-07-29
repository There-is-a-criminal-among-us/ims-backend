package kr.co.ksgk.ims.domain.S3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.S3.dto.request.PresignedUrlUploadRequest;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "S3", description = "S3 관련 API")
public interface S3Api {

    @Operation(
            summary = "Presigned URL 생성",
            description = "S3에 파일을 업로드하기 위한 Presigned URL을 생성합니다."
    )
    ResponseEntity<SuccessResponse<?>> getPresignedUrl(@RequestBody PresignedUrlUploadRequest request);
}
