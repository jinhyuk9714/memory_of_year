package com.demo.album.repository;

import com.demo.album.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    // 특정 앨범의 모든 편지 조회
    List<Letter> findByAlbumAlbumId(Long albumId);

    // 특정 앨범의 특정 편지 조회
    Optional<Letter> findByLetterIdAndAlbumAlbumId(Long letterId, Long albumId);
}
