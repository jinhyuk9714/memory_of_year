package com.demo.album.service;

import com.demo.album.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StickerService {

    private final S3Service s3Service;
    private final StickerRepository stickerRepository;

    public List<String> getAvailableStickers() {
        List<String> stickers = s3Service.listFilesInFolder("stickers/");
        log.debug("허용된 스티커 목록: {}", stickers);
        return stickers;
    }

    public boolean isValidStickerUrl(String stickerUrl) {
        return stickerUrl != null && getAvailableStickers().contains(stickerUrl);
    }
}
