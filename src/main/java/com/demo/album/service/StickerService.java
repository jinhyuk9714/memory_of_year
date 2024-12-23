package com.demo.album.service;

import com.demo.album.repository.StickerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import com.demo.album.entity.Sticker;

import java.util.stream.Collectors;

@Service
public class StickerService {

    private final S3Service s3Service;
    private final StickerRepository stickerRepository;

    @Autowired
    public StickerService(S3Service s3Service, StickerRepository stickerRepository) {
        this.s3Service = s3Service;
        this.stickerRepository = stickerRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(StickerService.class);

    public List<String> getAvailableStickers() {
        List<String> stickers = s3Service.listFilesInFolder("stickers/");
        logger.info("허용된 스티커 목록: {}", stickers);
        return stickers;
    }

    public boolean isValidStickerUrl(String stickerUrl) {
        return getAvailableStickers().contains(stickerUrl);
    }
}

