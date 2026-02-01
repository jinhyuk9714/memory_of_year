package com.demo.album.service;

import com.demo.album.dto.AlbumResponseDto;
import com.demo.album.entity.Album;
import com.demo.album.entity.User;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.repository.AlbumRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AlbumService 단위 테스트
 * - 앨범 생성/조회/수정, 소유 여부 포함 조회 검증
 */
@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private AlbumService albumService;

    private static User createUser(Long id) {
        return User.builder().userId(id).username("user").nickname("유저").email("a@b.com").role("USER").build();
    }

    private static Album createAlbum(Long albumId, Long ownerId) {
        User owner = createUser(ownerId);
        Album a = Album.builder()
                .albumId(albumId)
                .title("제목")
                .albumColor("#fff")
                .visibility(true)
                .stickerUrl("http://sticker")
                .owner(owner)
                .letters(new ArrayList<>())
                .build();
        return a;
    }

    @Nested
    @DisplayName("hasAlbum")
    class HasAlbum {

        @Test
        @DisplayName("소유 앨범이 있으면 true")
        void hasAlbum_true() {
            User user = createUser(1L);
            when(albumRepository.findByOwner(user)).thenReturn(Optional.of(createAlbum(1L, 1L)));

            boolean result = albumService.hasAlbum(user);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("소유 앨범이 없으면 false")
        void hasAlbum_false() {
            User user = createUser(1L);
            when(albumRepository.findByOwner(user)).thenReturn(Optional.empty());

            boolean result = albumService.hasAlbum(user);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("createAlbum")
    class CreateAlbum {

        @Test
        @DisplayName("앨범 생성 후 저장된 엔티티 반환")
        void success() {
            User user = createUser(1L);
            Album saved = createAlbum(1L, 1L);
            when(albumRepository.save(any(Album.class))).thenReturn(saved);

            Album result = albumService.createAlbum("제목", "#fff", true, "http://sticker", user);

            assertThat(result.getAlbumId()).isEqualTo(1L);
            verify(albumRepository).save(any(Album.class));
        }
    }

    @Nested
    @DisplayName("updateAlbum")
    class UpdateAlbum {

        @Test
        @DisplayName("존재하는 앨범의 title만 수정")
        void partialUpdate() {
            Album album = createAlbum(1L, 1L);
            when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
            when(albumRepository.save(any(Album.class))).thenAnswer(i -> i.getArgument(0));

            Album result = albumService.updateAlbum(1L, Map.of("title", "새제목"));

            assertThat(result.getTitle()).isEqualTo("새제목");
            verify(albumRepository).save(album);
        }

        @Test
        @DisplayName("앨범이 없으면 ResourceNotFoundException")
        void notFound() {
            when(albumRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> albumService.updateAlbum(999L, Map.of("title", "x")))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("앨범");
        }
    }

    @Nested
    @DisplayName("getAlbumWithOwnership")
    class GetAlbumWithOwnership {

        @Test
        @DisplayName("본인 앨범이면 isOwnAlbum true")
        void ownAlbum() {
            Album album = createAlbum(1L, 1L);
            when(albumRepository.findByIdWithOwnerAndLetters(1L)).thenReturn(Optional.of(album));

            AlbumResponseDto dto = albumService.getAlbumWithOwnership(1L, 1L);

            assertThat(dto.getAlbumId()).isEqualTo(1L);
            assertThat(dto.isOwnAlbum()).isTrue();
        }

        @Test
        @DisplayName("타인 앨범이면 isOwnAlbum false")
        void otherAlbum() {
            Album album = createAlbum(1L, 1L); // owner userId=1
            when(albumRepository.findByIdWithOwnerAndLetters(1L)).thenReturn(Optional.of(album));

            AlbumResponseDto dto = albumService.getAlbumWithOwnership(1L, 2L);

            assertThat(dto.isOwnAlbum()).isFalse();
        }

        @Test
        @DisplayName("앨범 없으면 ResourceNotFoundException")
        void notFound() {
            when(albumRepository.findByIdWithOwnerAndLetters(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> albumService.getAlbumWithOwnership(999L, 1L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
