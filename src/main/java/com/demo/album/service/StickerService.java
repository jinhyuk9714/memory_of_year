package com.demo.album.service;

import com.demo.album.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 스티커 비즈니스 로직
 * - S3 stickers/ 폴더 기반으로 사용 가능 스티커 URL 목록 조회
 * - URL이 허용 목록에 있는지 검증 (앨범/사진 생성 시 스티커 URL 검증용)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StickerService {

    private final S3Service s3Service;
    private final StickerRepository stickerRepository;

    /** S3 stickers/ 폴더 내 파일 URL 목록 반환 */
    public List<String> getAvailableStickers() {
        List<String> stickers = s3Service.listFilesInFolder("stickers/");
        log.debug("허용된 스티커 목록: {}", stickers);
        return stickers;
    }

    /** stickerUrl이 허용 목록에 포함되어 있는지 여부 (null이면 false) */
    public boolean isValidStickerUrl(String stickerUrl) {
        return stickerUrl != null && getAvailableStickers().contains(stickerUrl);
    }
}
