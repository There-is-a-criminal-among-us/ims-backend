package kr.co.ksgk.ims.domain.S3.controller;

import kr.co.ksgk.ims.domain.S3.dto.request.PresignedUrlUploadRequest;
import kr.co.ksgk.ims.domain.S3.dto.response.PresignedUrlUploadResponse;
import kr.co.ksgk.ims.domain.S3.service.S3Service;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller implements S3Api {

    private final S3Service s3Service;

    @PostMapping("/presigned/upload")
    public ResponseEntity<SuccessResponse<?>> getPresignedUrl(@RequestBody PresignedUrlUploadRequest request) {
        PresignedUrlUploadResponse presignedUrlUploadResponse = s3Service.getPresignedUrl(request);
        return SuccessResponse.ok(presignedUrlUploadResponse);
    }
}


