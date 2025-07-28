package com.chungnamthon.cheonon.global.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/heic",
            "image/gif"
    );

    public S3UploadResult uploadFile(MultipartFile file) {
        validateImageFormat(file);
        String originalFileName = file.getOriginalFilename();
        String storedFileName = createFileName(originalFileName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            amazonS3Client.putObject(new PutObjectRequest(
                    bucket,
                    storedFileName,
                    file.getInputStream(),
                    metadata
            ));
        } catch (IOException e) {
            throw new RuntimeException("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

        String fileUrl = amazonS3Client.getUrl(bucket, storedFileName).toString();
        return new S3UploadResult(originalFileName, storedFileName, fileUrl);
    }

    private void validateImageFormat(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("지원하지 않는 이미지 형식입니다. jpg, jpeg, png, HEIC, gif 형식만 지원합니다.");
        }
    }

    private String createFileName(String originalFileName) {
        try {
            return UUID.randomUUID().toString() + getFileExtension(originalFileName);
        } catch (StringIndexOutOfBoundsException e) {
            throw new RuntimeException("잘못된 파일명입니다.");
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Getter
    @AllArgsConstructor
    public static class S3UploadResult {
        private String originalFileName;
        private String storedFileName;
        private String fileUrl;
    }
}