package com.demo.album.repository;

import com.demo.album.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Sticker 엔티티용 JPA Repository
 * - 현재 스티커 목록은 S3 폴더(stickers/) 기반으로 StickerService에서 제공
 */
public interface StickerRepository extends JpaRepository<Sticker, Long> {
}
