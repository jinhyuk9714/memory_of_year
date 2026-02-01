package com.demo.album.repository;

import com.demo.album.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Letter 엔티티용 JPA Repository
 * - findByAlbumAlbumId: 앨범별 편지 목록
 * - findByLetterIdAndAlbumAlbumId: 앨범+편지 ID로 상세 조회 (권한 검증용)
 */
public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findByAlbumAlbumId(Long albumId);

    Optional<Letter> findByLetterIdAndAlbumAlbumId(Long letterId, Long albumId);
}
