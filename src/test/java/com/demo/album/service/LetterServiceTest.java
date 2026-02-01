package com.demo.album.service;

import com.demo.album.dto.LetterRequestDto;
import com.demo.album.entity.Album;
import com.demo.album.entity.Letter;
import com.demo.album.entity.User;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.repository.AlbumRepository;
import com.demo.album.repository.LetterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * LetterService 단위 테스트
 * - 편지 생성, 앨범별 목록/상세 조회 검증
 */
@ExtendWith(MockitoExtension.class)
class LetterServiceTest {

    @Mock
    private LetterRepository letterRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private LetterService letterService;

    private static Album createAlbum(Long id) {
        User owner = User.builder().userId(1L).username("u").build();
        return Album.builder().albumId(id).title("앨범").owner(owner).build();
    }

    @Nested
    @DisplayName("createLetter")
    class CreateLetter {

        @Test
        @DisplayName("앨범이 있으면 편지 생성 후 저장")
        void success() {
            Long albumId = 1L;
            Album album = createAlbum(albumId);
            when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

            LetterRequestDto dto = new LetterRequestDto();
            dto.setLetterTitle("제목");
            dto.setAuthor("작성자");
            dto.setContent("내용");
            dto.setAnonymous(false);
            dto.setLetterColor("#fff");

            Letter saved = Letter.builder().letterId(1L).letterTitle("제목").album(album).build();
            when(letterRepository.save(any(Letter.class))).thenReturn(saved);

            Letter result = letterService.createLetter(albumId, dto);

            assertThat(result.getLetterId()).isEqualTo(1L);
            verify(albumRepository).findById(albumId);
            verify(letterRepository).save(any(Letter.class));
        }

        @Test
        @DisplayName("앨범이 없으면 ResourceNotFoundException")
        void albumNotFound() {
            when(albumRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> letterService.createLetter(999L, new LetterRequestDto()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("앨범");
        }
    }

    @Nested
    @DisplayName("getLettersByAlbumId")
    class GetLettersByAlbumId {

        @Test
        @DisplayName("앨범 ID로 편지 목록 반환")
        void success() {
            List<Letter> letters = List.of(
                    Letter.builder().letterId(1L).letterTitle("a").build(),
                    Letter.builder().letterId(2L).letterTitle("b").build()
            );
            when(letterRepository.findByAlbumAlbumId(1L)).thenReturn(letters);

            List<Letter> result = letterService.getLettersByAlbumId(1L);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("getLetterByIdAndAlbumId")
    class GetLetterByIdAndAlbumId {

        @Test
        @DisplayName("편지 ID와 앨범 ID로 조회")
        void success() {
            Letter letter = Letter.builder().letterId(1L).letterTitle("제목").build();
            when(letterRepository.findByLetterIdAndAlbumAlbumId(1L, 1L)).thenReturn(Optional.of(letter));

            Optional<Letter> result = letterService.getLetterByIdAndAlbumId(1L, 1L);

            assertThat(result).isPresent();
            assertThat(result.get().getLetterId()).isEqualTo(1L);
        }
    }
}
