package com.demo.album.repository;

import com.demo.album.entity.Album;
import com.demo.album.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Album 엔티티용 JPA Repository
 * - findByOwner: 소유자별 앨범 조회 (1인 1앨범 여부 확인용)
 * - findByIdWithOwnerAndLetters: N+1 방지용 (owner, letters 한 번에 fetch)
 */
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByOwner(User owner);

    Optional<Album> findByAlbumIdAndOwner(Long albumId, User owner);

    /** owner, letters를 함께 fetch하여 N+1 쿼리 방지 */
    @EntityGraph(attributePaths = {"owner", "letters"})
    @Query("SELECT a FROM Album a WHERE a.albumId = :albumId")
    Optional<Album> findByIdWithOwnerAndLetters(@Param("albumId") Long albumId);
}
