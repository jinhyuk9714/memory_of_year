package com.demo.album.repository;

import com.demo.album.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Photo 엔티티용 JPA Repository
 * - findByLetter_LetterId: 편지별 사진 목록
 * - findByPhotoIdAndLetter_LetterId: 편지+사진 ID로 상세 조회 (권한 검증용)
 */
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByLetter_LetterId(Long letterId);

    Optional<Photo> findByPhotoIdAndLetter_LetterId(Long photoId, Long letterId);
}
