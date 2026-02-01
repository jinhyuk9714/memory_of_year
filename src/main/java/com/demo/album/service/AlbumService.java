package com.demo.album.service;

import com.demo.album.dto.AlbumResponseDto;
import com.demo.album.entity.Album;
import com.demo.album.entity.User;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;

    public boolean hasAlbum(User user) {
        return albumRepository.findByOwner(user).isPresent();
    }

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
