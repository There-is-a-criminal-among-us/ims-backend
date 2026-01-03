package kr.co.ksgk.ims.domain.S3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import kr.co.ksgk.ims.domain.S3.dto.request.PresignedUrlUploadRequest;
import kr.co.ksgk.ims.domain.S3.dto.response.PresignedUrlUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public PresignedUrlUploadResponse getPresignedUrl(PresignedUrlUploadRequest request) {
        Date expiration = new Date();
        long expTime = expiration.getTime();
        expTime += TimeUnit.MINUTES.toMillis(3);
        expiration.setTime(expTime);

        String keyName = UUID.randomUUID() + "_" + request.fileName();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, keyName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        String url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
        String key = generatePresignedUrlRequest.getKey();

        return PresignedUrlUploadResponse.of(url, key);
    }

    public String generateStaticUrl(String keyName) {
        return "https://" + region + ".vultrobjects.com/" + bucket + "/" + keyName;
    }
}
