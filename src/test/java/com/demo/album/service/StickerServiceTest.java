package com.demo.album.service;

import com.demo.album.repository.StickerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * StickerService 단위 테스트
 * - S3 폴더 기반 스티커 목록, URL 유효성 검증
 */
@ExtendWith(MockitoExtension.class)
class StickerServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private StickerRepository stickerRepository;

    @InjectMocks
    private StickerService stickerService;

    @Nested
    @DisplayName("getAvailableStickers")
    class GetAvailableStickers {

        @Test
        @DisplayName("S3 stickers/ 폴더 URL 목록 반환")
        void success() {
            List<String> urls = List.of(
                    "https://bucket.s3.region.amazonaws.com/stickers/a.png",
                    "https://bucket.s3.region.amazonaws.com/stickers/b.png"
            );
            when(s3Service.listFilesInFolder("stickers/")).thenReturn(urls);

            List<String> result = stickerService.getAvailableStickers();

            assertThat(result).hasSize(2);
            assertThat(result).contains(urls.get(0));
        }
    }

    @Nested
    @DisplayName("isValidStickerUrl")
    class IsValidStickerUrl {

        @Test
        @DisplayName("허용 목록에 있으면 true")
        void valid() {
            String url = "https://bucket.s3.region.amazonaws.com/stickers/a.png";
            when(s3Service.listFilesInFolder("stickers/")).thenReturn(List.of(url));

            boolean result = stickerService.isValidStickerUrl(url);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("허용 목록에 없으면 false")
        void invalid() {
            when(s3Service.listFilesInFolder("stickers/")).thenReturn(List.of("https://other.png"));

            boolean result = stickerService.isValidStickerUrl("https://invalid.png");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("null이면 false")
        void nullUrl() {
            boolean result = stickerService.isValidStickerUrl(null);

            assertThat(result).isFalse();
        }
    }
}
