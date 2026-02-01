package com.demo.album.repository;

import com.demo.album.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Letter 엔티티용 JPA Repository
 * - findByAlbumAlbumId: 앨범별 편지 목록
 * - findLettersWithPhotoCountByAlbumId: 편지 + 사진 개수 한 번에 조회 (N+1 방지)
 * - findByLetterIdAndAlbumAlbumId: 앨범+편지 ID로 상세 조회 (권한 검증용)
 */
public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findByAlbumAlbumId(Long albumId);

    /** 편지 + 사진 개수를 1회 쿼리로 조회 (서브쿼리 COUNT) */
    @Query("SELECT l, (SELECT COUNT(p) FROM Photo p WHERE p.letter = l) FROM Letter l WHERE l.album.albumId = :albumId ORDER BY l.createdAt")
    List<Object[]> findLettersWithPhotoCountByAlbumId(@Param("albumId") Long albumId);

    Optional<Letter> findByLetterIdAndAlbumAlbumId(Long letterId, Long albumId);
}
