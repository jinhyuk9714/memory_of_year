package com.demo.album.repository;

import com.demo.album.entity.Album;
import com.demo.album.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Album 엔티티용 JPA Repository
 * - findByOwner: 소유자별 앨범 조회 (1인 1앨범 여부 확인용)
 */
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByOwner(User owner);

    Optional<Album> findByAlbumIdAndOwner(Long albumId, User owner);
}
