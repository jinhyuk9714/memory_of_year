package com.demo.album.service;

import com.demo.album.entity.Letter;
import com.demo.album.entity.Photo;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.repository.LetterRepository;
import com.demo.album.repository.PhotoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PhotoService 단위 테스트
 * - 사진 추가(편지 조회 + S3 업로드 + 저장), 목록/상세 조회 검증
 */
@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private LetterRepository letterRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private PhotoService photoService;

    @Nested
    @DisplayName("addPhoto")
    class AddPhoto {

        @Test
        @DisplayName("편지가 있으면 S3 업로드 후 Photo 저장")
        void success() throws IOException {
            Letter letter = Letter.builder().letterId(1L).build();
            when(letterRepository.findById(1L)).thenReturn(Optional.of(letter));
            when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn("https://s3/photo.jpg");

            Photo saved = Photo.builder().photoId(1L).url("https://s3/photo.jpg").letter(letter).build();
            when(photoRepository.save(any(Photo.class))).thenReturn(saved);

            MultipartFile file = mock(MultipartFile.class);

            Photo result = photoService.addPhoto(1L, file, "코멘트", null);

            assertThat(result.getPhotoId()).isEqualTo(1L);
            verify(letterRepository).findById(1L);
            verify(s3Service).uploadFile(file);
            verify(photoRepository).save(any(Photo.class));
        }

        @Test
        @DisplayName("편지가 없으면 ResourceNotFoundException")
        void letterNotFound() {
            when(letterRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> photoService.addPhoto(999L, mock(MultipartFile.class), null, null))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("편지");
        }
    }

    @Nested
    @DisplayName("getPhotosByLetterId")
    class GetPhotosByLetterId {

        @Test
        @DisplayName("편지 ID로 사진 목록 반환")
        void success() {
            List<Photo> photos = List.of(
                    Photo.builder().photoId(1L).url("u1").build(),
                    Photo.builder().photoId(2L).url("u2").build()
            );
            when(photoRepository.findByLetter_LetterId(1L)).thenReturn(photos);

            List<Photo> result = photoService.getPhotosByLetterId(1L);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("getPhotoByIdAndLetterId")
    class GetPhotoByIdAndLetterId {

        @Test
        @DisplayName("사진 ID와 편지 ID로 조회")
        void success() {
            Photo photo = Photo.builder().photoId(1L).url("u").build();
            when(photoRepository.findByPhotoIdAndLetter_LetterId(1L, 1L)).thenReturn(Optional.of(photo));

            Optional<Photo> result = photoService.getPhotoByIdAndLetterId(1L, 1L);

            assertThat(result).isPresent();
            assertThat(result.get().getPhotoId()).isEqualTo(1L);
        }
    }
}
