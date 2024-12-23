package com.demo.album.repository;

import com.demo.album.entity.Album;
import com.demo.album.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    // 특정 소유자가 가진 앨범을 찾는 메서드
    Optional<Album> findByOwner(User owner);

    // 앨범 ID와 소유자 정보로 앨범을 찾는 메서드
    Optional<Album> findByAlbumIdAndOwner(Long albumId, User owner);
}
