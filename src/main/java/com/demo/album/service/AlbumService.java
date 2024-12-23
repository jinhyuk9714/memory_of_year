package com.demo.album.service;

import com.demo.album.dto.AlbumResponseDto;
import com.demo.album.entity.Album;
import com.demo.album.entity.User;
import com.demo.album.repository.AlbumRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public boolean hasAlbum(User user) {
        return albumRepository.findByOwner(user).isPresent();
    }

    public Album createAlbum(String title, String albumColor, boolean visibility, String stickerUrl, User user) {
        Album album = new Album(title, albumColor, visibility, stickerUrl, user);
        return albumRepository.save(album);
    }

    public Album updateAlbum(Long albumId, Map<String, Object> updates) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

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

    @Transactional
    public AlbumResponseDto getAlbumWithOwnership(Long albumId, Long userId) {
        Optional<Album> albumOpt = albumRepository.findById(albumId);

        if (albumOpt.isPresent()) {
            Album album = albumOpt.get();
            boolean isOwnAlbum = album.getOwner().getUserId().equals(userId);

            return new AlbumResponseDto(
                    album.getAlbumId(),
                    album.getTitle(),
                    album.getAlbumColor(),
                    album.isVisibility(),
                    album.getLetters().size(),
                    album.getStickerUrl(), // 추가된 매개변수
                    isOwnAlbum
            );
        } else {
            throw new IllegalArgumentException("앨범을 찾을 수 없습니다.");
        }
    }


}
