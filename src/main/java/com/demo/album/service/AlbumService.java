package com.demo.album.service;

import com.demo.album.dto.AlbumResponseDto;
import com.demo.album.entity.Album;
import com.demo.album.entity.User;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 앨범 비즈니스 로직
 * - 소유 여부 확인, 앨범 생성/수정, 앨범 조회(소유 여부 포함 DTO)
 */
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;

    /** 해당 사용자가 소유한 앨범이 있는지 여부 */
    public boolean hasAlbum(User user) {
        return albumRepository.findByOwner(user).isPresent();
    }

    /** 앨범 생성 후 저장 */
    public Album createAlbum(String title, String albumColor, boolean visibility, String stickerUrl, User user) {
        Album album = Album.builder()
                .title(title)
                .albumColor(albumColor)
                .visibility(visibility)
                .stickerUrl(stickerUrl)
                .owner(user)
                .build();
        return albumRepository.save(album);
    }

    /** 앨범 부분 수정 (title, albumColor, visibility, stickerUrl만 반영) */
    public Album updateAlbum(Long albumId, Map<String, Object> updates) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("앨범", albumId));

        if (updates.containsKey("title")) {
            album.setTitle((String) updates.get("title"));
        }
        if (updates.containsKey("albumColor")) {
            album.setAlbumColor((String) updates.get("albumColor"));
        }
        if (updates.containsKey("visibility")) {
            album.setVisibility((Boolean) updates.get("visibility"));
        }
        if (updates.containsKey("stickerUrl")) {
            album.setStickerUrl((String) updates.get("stickerUrl"));
        }

        return albumRepository.save(album);
    }

    /** 앨범 조회 + 현재 사용자 소유 여부(isOwnAlbum) 포함 DTO 반환 */
    @Transactional(readOnly = true)
    public AlbumResponseDto getAlbumWithOwnership(Long albumId, Long userId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("앨범", albumId));

        boolean isOwnAlbum = album.getOwner().getUserId().equals(userId);

        return new AlbumResponseDto(
                album.getAlbumId(),
                album.getTitle(),
                album.getAlbumColor(),
                album.isVisibility(),
                album.getLetters().size(),
                album.getStickerUrl(),
                isOwnAlbum
        );
    }
}
