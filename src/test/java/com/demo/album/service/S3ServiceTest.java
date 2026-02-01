package com.demo.album.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * S3Service 단위 테스트
 * - uploadFile, listFilesInFolder 동작 검증 (S3Client Mock)
 */
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    private S3Service s3Service;

    private static final String BUCKET = "test-bucket";
    private static final String REGION = "ap-northeast-2";

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client);
        ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET);
        ReflectionTestUtils.setField(s3Service, "region", REGION);
    }

    @Nested
    @DisplayName("uploadFile")
    class UploadFile {

        @Test
        @DisplayName("파일 업로드 시 S3 putObject 호출 후 URL 반환")
        void success() throws Exception {
            byte[] bytes = "hello".getBytes();
            org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
            when(file.getBytes()).thenReturn(bytes);
            when(file.getOriginalFilename()).thenReturn("photo.jpg");

            String url = s3Service.uploadFile(file);

            assertThat(url).startsWith("https://" + BUCKET + ".s3.amazonaws.com/");
            assertThat(url).contains("photo.jpg");
            verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }
    }

    @Nested
    @DisplayName("listFilesInFolder")
    class ListFilesInFolder {

        @Test
        @DisplayName("prefix로 목록 조회 후 전체 URL 리스트 반환")
        void success() {
            S3Object obj1 = S3Object.builder().key("stickers/a.png").build();
            S3Object obj2 = S3Object.builder().key("stickers/b.png").build();
            ListObjectsV2Response response = ListObjectsV2Response.builder()
                    .contents(List.of(obj1, obj2))
                    .build();
            when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);

            List<String> urls = s3Service.listFilesInFolder("stickers/");

            assertThat(urls).hasSize(2);
            assertThat(urls.get(0)).contains(BUCKET).contains(REGION).contains("stickers/a.png");
        }
    }

    @Nested
    @DisplayName("deleteFile")
    class DeleteFile {

        @Test
        @DisplayName("deleteObject 호출")
        void success() {
            s3Service.deleteFile("key123");

            verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        }
    }
}
