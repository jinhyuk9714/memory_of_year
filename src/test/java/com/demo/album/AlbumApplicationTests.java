package com.demo.album;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Spring Boot 애플리케이션 컨텍스트 로드 테스트
 * - test 프로필: H2 인메모리 DB 사용
 * - S3Client는 Mock으로 대체하여 AWS 의존성 제거
 */
@SpringBootTest
@ActiveProfiles("test")
class AlbumApplicationTests {

    @MockBean
    private S3Client s3Client;

    @Test
    @DisplayName("컨텍스트가 정상 로드된다")
    void contextLoads() {
    }
}
